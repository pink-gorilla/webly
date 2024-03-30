(ns webly.module.build
  (:require-macros
   [webly.module.build :refer [get-lazy-modules get-lazy-ns set-ns-loadables! set-ns-loadables2! set-ns-vars!]])
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

(defonce lazy-ns-loadable2-a (atom {}))


 (set-ns-loadables2!)

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
    (set-ns-loadables!)
    (set-ns-vars!)
   
    (println "compile-time lazy-ns-a: " @lazy-ns-a)
    ;(println "compile-time loadable-config: " loadable-spec)
    (println "compile-time lazy-loadable-a: " @lazy-ns-loadable-a)
    :ok))

(defn print-build-summary []
  (println "webly-build summary:")
  (println "lazy modules: " (keys @lazy-modules-a))
  (println "lazy namespaces: " (keys @lazy-ns-loadable-a))
  (println "lazy-ns-vars: " (keys @lazy-ns-vars-a))
  (println "lazy-ns-loadable: "  @lazy-ns-loadable2-a)
  )


(defn load-namespace
  "returns a promise containing 
   a map. keys = ns publics, values = vars"
  [ns-name]
  (let [{:keys [loadable ns-vars]} (get @lazy-ns-loadable-a ns-name)
        rp (p/deferred)
        on-error (fn [err]
                   (println "could not load ns: " ns-name "! ERROR!")
                   (println "ns load error: " err)
                   (p/reject! rp err))
        on-success (fn [vars]
                     (info "ns [" ns-name "] loaded successfully!")
                     (let [ns-map (->> (map (fn [n v]
                                              [n v]) ns-vars vars) ; @loadable
                                       (into {}))]
                       (println "ns-map: " ns-map)
                       (p/resolve! rp ns-map)))]
    ; lazy/load does return a google deferred, so we cannot use promises here.
    (try
      (shadow.lazy/load loadable on-success on-error)
      (catch :default ex
        (error "shadow.lazy/load could not load ns: " ns-name "error: " ex)
        (p/reject! rp ex)))
    rp))