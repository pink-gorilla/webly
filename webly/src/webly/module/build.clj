(ns webly.module.build
  (:require
   [clojure.string :as str]
   [clojure.set]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [extension :refer [get-extensions write-service]]))

;; NAMESPACE

(defn ->keyword [s]
  (-> s str (str/replace  #"'" "") keyword))

(defn- convert-ns-def [module-name ns-def]
  (if (map? ns-def)
    {:module module-name
     :ns-vars (->> (keys ns-def) (map ->keyword) (into []))
     :loadable (->> (vals ns-def) (into []))}
    {:module module-name
     :loadable ns-def}))

(defn- convert-ns [module-name [ns-name ns-def]]
  ;(println "ns-name: " (pr-str ns-name) "keyword: " (->keyword ns-name))
  [(->keyword ns-name) (convert-ns-def module-name ns-def)])

(defn- module->ns [{:keys [name cljs-ns-bindings]}]
  ; namespaces per module is needed to find the module that needs to be loaded for a ns
  (map #(convert-ns name %) cljs-ns-bindings))

(defn modules->ns-map [modules]
  (->> (reduce concat [] (map module->ns modules))
       (into {})))

(defn ns-map->vars [ns-map]
  (->> (map (fn [[ns-name {:keys [ns-vars]}]]
              (when ns-vars
                [ns-name ns-vars])) ns-map)
       (remove nil?)
       (into {})))

(defn ns-map->loadable [ns-map]
  (->> (map (fn [[ns-name {:keys [loadable]}]]
              (when loadable
                [ns-name loadable])) ns-map)
       (remove nil?)
       (into {})))

;; lazy namespace

(defonce lazy-modules-a (atom []))
(defonce lazy-ns-a (atom {}))
(defonce lazy-ns-vars-a (atom {}))
(defonce lazy-ns-loadable-a (atom {}))

(defmacro get-lazy-modules []
  (warn "lazy modules:" @lazy-modules-a)
  (into [] @lazy-modules-a))

(defmacro get-lazy-ns []
  (let [l (keys @lazy-ns-a)]
    (warn "lazy namespaces: " l)
    (into [] l)))

(defn- set-lazy-modules! [exts lazy-modules]
  (let [spec (modules->ns-map lazy-modules)
        ns-vars (ns-map->vars spec)
        ns-loadable (ns-map->loadable spec)]
    (write-service exts :cljsbuild-lazy-namespaces spec)
    (write-service exts :cljsbuild-lazy-ns-vars ns-vars)
    (write-service exts :cljsbuild-lazy-ns-loadable ns-loadable)
    (reset! lazy-modules-a (map :name lazy-modules))
    (reset! lazy-ns-a spec)
    (reset! lazy-ns-vars-a ns-vars)
    (reset! lazy-ns-loadable-a ns-loadable)))

(defmacro set-ns-vars! []
  (let [ns-vars @lazy-ns-vars-a]
    ;`(reset! webly.module.build/lazy-ns-vars-a ~ns-vars)
    `(webly.module.build/set-ns-vars ~ns-vars)))

#_(defmacro set-ns-loadables-test! []
    ;specs
    ; lazy/loadable macro. It expects one argument which is 
    ; - a qualified symbol, 
    ; - a vector of symbols or
    ; - a map of keyword to symbol.
    `(reset! webly.module.build/lazy-ns-loadable-a
             {:'bongistan.core (shadow.lazy/loadable
                                [snippets.snip/add
                                 snippets.snip/ui-add])}))

(defmacro set-ns-loadables! []
  (let [loadables @lazy-ns-loadable-a]
    ;specs
    ; lazy/loadable macro. It expects one argument which is 
    ; - a qualified symbol, 
    ; - a vector of symbols or
    ; - a map of keyword to symbol.
    ;`(reset! webly.module.build/lazy-ns-loadable-a
    `(webly.module.build/set-ns-loadables
      ~(->> (map (fn [[ns-kw l]]
                   `[~ns-kw (shadow.lazy/loadable ~l)]) loadables)
            (into {})))))

(comment
  (str 'clojure.core)
  (name 'clojure.core)
  (symbol "clojure.core")

  (map str ['a 'bingo.bongo 'ui.highcharts])
  (map name ['a 'bingo.bongo 'ui.highcharts]))

;; SERVICE

(defn- module? [{:keys [cljs-namespace]}]
  (> (count cljs-namespace) 0))

(defn- lazy-module? [{:keys [lazy]}]
  lazy)

(defn create-modules
  "processes discovered extensions
   outputs a state that contains module information
   consider it the start-fn of a service."
  [exts]
  (let [modules (get-extensions exts {:name "unknown"
                                      :lazy false
                                      :cljs-namespace []
                                      :cljs-ns-bindings {}
                                      :depends-on #{}
                                      })
        valid-modules (filter module? modules)
        lazy-modules (filter lazy-module? valid-modules)
        main-modules (remove lazy-module? valid-modules)]
    (write-service exts :cljsbuild-module-lazy lazy-modules)
    (write-service exts :cljsbuild-module-main main-modules)
    (set-lazy-modules! exts lazy-modules)
    {:modules {:main main-modules
               :lazy lazy-modules}}))

;; SHADOW CONFIG

(defn- main-shadow-module [main-modules]
  (let [entries (->> (map :cljs-namespace main-modules)
                     (apply concat)
                     (into []))]
    [:webly {:entries entries}]))

(defn- lazy-shadow-module [{:keys [name cljs-namespace depends-on]}]
  (let [depends-on (clojure.set/union #{:webly} depends-on)]
  (println "module: " name " depends-on: " depends-on)  
  [(keyword name) {:entries (vec cljs-namespace)
                   :depends-on depends-on}]  
    ))

(defn shadow-module-config
  "input: the state created by create-modules
   output: the :modules section of the shadow-config"
  [{:keys [modules]}]
  (let [{:keys [main lazy]} modules
        modules-lazy (map lazy-shadow-module lazy)
        module-main (main-shadow-module main)
        modules (conj modules-lazy module-main)]
    (into {} modules)))


