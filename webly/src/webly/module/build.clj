(ns webly.module.build
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [extension :refer [get-extensions write-service]]))

;; lazy namespace

(defonce lazy-ns-a (atom {}))

(defn add-lazy-module-namespaces [{:keys [name cljs-ns-bindings]}]
  (let [ns-map (->> (map (fn [[ns-name ns-def]]
                           [(pr-str ns-name) {:module name
                                     :ns-def (pr-str ns-def)}])
                         cljs-ns-bindings) ; namespaces per module is needed to find the module that needs to be loaded for a ns
                    (into {}))]
    (swap! lazy-ns-a merge ns-map)))

(defmacro get-lazy-ns []
  (warn "lazy namespaces:" @lazy-ns-a)
  @lazy-ns-a)


(defmacro define-lazy-ns [ns-name]
  (let [m (get @lazy-ns-a ns-name)]
    `(shadow.lazy/loadable ~m)))

;; lazy modules

(defonce lazy-modules-a (atom []))

(defn- add-lazy-module [{:keys [name] :as module}]
  (add-lazy-module-namespaces module)
  (swap! lazy-modules-a conj name))

(defmacro get-lazy-modules []
  (warn "lazy modules:" @lazy-modules-a)
  (into [] @lazy-modules-a))






;; module

(defn module? [{:keys [cljs-namespace]}]
  (> (count cljs-namespace) 0))

(defn lazy-module? [{:keys [lazy-sci]}]
  lazy-sci)

(defn create-modules [exts]
  (let [modules (get-extensions exts {:name "unknown"
                                      :lazy-sci false
                                      :cljs-namespace []
                                      :cljs-ns-bindings {}})
        valid-modules (filter module? modules)
        lazy-modules (filter lazy-module? valid-modules)
        main-modules (remove lazy-module? valid-modules)]
    (write-service exts :cljsbuild-module-lazy lazy-modules)
    (write-service exts :cljsbuild-module-main main-modules)
    (doall (map add-lazy-module lazy-modules))
    ;(reset! lazy-modules-a lazy-modules)
    {:modules {:main main-modules
               :lazy lazy-modules}}))


(defn main-shadow-module [main-modules]
  (let [entries (->> (map :cljs-namespace main-modules)
                     (apply concat)
                     (into []))]
    [:webly {:entries entries}]))

(defn lazy-shadow-module [{:keys [name cljs-namespace]}]
  [(keyword name) {:entries (vec cljs-namespace)
                   :depends-on #{:webly}}])

(defn shadow-module-config [{:keys [modules]}]
  (let [{:keys [main lazy]} modules
        modules-lazy (map lazy-shadow-module lazy)
        module-main (main-shadow-module main)
        modules (conj modules-lazy module-main)]
    (into {} modules)))


