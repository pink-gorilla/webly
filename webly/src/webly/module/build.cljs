(ns webly.module.build
  (:require-macros
   [webly.module.build :refer [get-lazy-modules get-lazy-ns set-ns-loadables! set-ns-vars!]])
  (:require
   [promesa.core :as p]
   [taoensso.timbre :refer-macros [debug info warn error]]
   [shadow.lazy]))


(defonce lazy-modules-a (atom {}))
; key: module-name
; val: false/true (is-loaded?)

(defn module-list->map [module-list]
  (->> (map (fn [n] [n false]) module-list)
       (into {})))

(defonce lazy-ns-a (atom []))
; a seq of strings (equaling to ns-names)

(defonce lazy-ns-loadable-a (atom {}))
(defonce lazy-ns-vars-a (atom {}))


(set-ns-loadables!)
(set-ns-vars!)

(defn add-lazy-modules
  ;"adds modules to the build. Needs to be called from the cljs-app."
  []
  (println "************ add-lazy-modules .....")
  (let [modules (get-lazy-modules)
        modules-map (module-list->map modules)
        ns-list (get-lazy-ns)
        ;loadable-spec 
        ]
    (reset! lazy-modules-a modules-map)
    (reset! lazy-ns-a ns-list)


    (println "compile-time lazy-ns-a: " @lazy-ns-a)
    ;(println "compile-time loadable-config: " loadable-spec)
    (println "compile-time lazy-loadable-a: " @lazy-ns-loadable-a)
    :ok))

(defn print-build-summary []
  (info "webly-build summary:")
  (info "lazy modules: " (keys @lazy-modules-a))
  (info "lazy-ns-loadable: " (keys @lazy-ns-loadable-a))
  ;(info "lazy-ns-loadable FULL: "  @lazy-ns-loadable-a)
  (info "lazy-ns-vars: " (keys @lazy-ns-vars-a)))


(defn load-namespace-raw
  "returns a promise containing the resolved loadables for a namespace"
  [ns-name]
  (let [loadable  (get @lazy-ns-loadable-a ns-name)
        rp (p/deferred)
        on-error (fn [err]
                   (error "could not load ns: " ns-name "! ERROR: " err)
                   (p/reject! rp err))
        on-success (fn [vars]
                     (info "ns [" ns-name "] loaded successfully!")
                     (p/resolve! rp vars))]
    ; lazy/load does return a google deferred, so we cannot use promises here.
    (try
      (shadow.lazy/load loadable on-success on-error)
      (catch :default ex
        (error "shadow.lazy/load could not load ns: " ns-name "error: " ex)
        (p/reject! rp ex)))
    rp))


(defn- ns-assemble [ns-vars vars]
  (->> (map (fn [n v]
              [n v]) ns-vars vars)
       (into {})))

(defn load-namespace
  "returns a promise containing 
   a map. keys = ns publics, values = vars"
  [ns-name]
  (let [ns-vars (get @lazy-ns-vars-a ns-name)
        rp (p/deferred)]
    (if ns-vars
      (let [rp2 (load-namespace-raw ns-name)]
        (-> rp2
            (p/then (fn [vars]
                      (info "load-namespace vars successfully received!")
                      (p/resolve! rp (ns-assemble ns-vars vars))))))
      (p/reject! rp (str "cannot load-namespace: " ns-name " - no ns-map defined (sci-config ns)")))
    rp))