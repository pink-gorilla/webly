(ns shadowx.module.build
  (:require-macros
   [shadowx.module.build :refer [get-lazy-modules get-lazy-ns set-ns-loadables! set-ns-vars!]])
  (:require
   [promesa.core :as p]
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

(defn adjust-ns-def [ns-def]
  ;(info "adjusting ns-def: " ns-def)
  (->> (map symbol ns-def)
       (into [])))

(defn set-ns-vars [ns-map]
  (let [ns-map (->> (map (fn [[ns-kw ns-def]]
                           [(symbol ns-kw) (adjust-ns-def ns-def)])
                         ns-map)
                    (into {}))]
    ;(warn "modified ns-map: " ns-map)
    (reset! lazy-ns-vars-a ns-map)))

(defn set-ns-loadables [ns-map]
  (let [ns-map (->> (map (fn [[ns-kw l-def]]
                           [(symbol ns-kw) l-def])
                         ns-map)
                    (into {}))]
    (reset! lazy-ns-loadable-a ns-map)))

(set-ns-loadables!)
(set-ns-vars!)

(defn add-lazy-modules
  ;"adds modules to the build. Needs to be called from the cljs-app."
  []
  ;(println "add-lazy-modules .....")
  (let [modules (get-lazy-modules)
        modules-map (module-list->map modules)
        ns-list (get-lazy-ns)
        ;loadable-spec 
        ]
    (reset! lazy-modules-a modules-map)
    (reset! lazy-ns-a ns-list)
    ;(println "compile-time lazy-ns-a: " (sort @lazy-ns-a))
    :ok))

(defn print-build-summary []
  (println (str "webly-build summary:"
                "\nlazy modules: " (-> @lazy-modules-a keys sort)
                "\nlazy-ns-loadable: " (-> @lazy-ns-loadable-a keys sort)
                ;"\nlazy-ns-vars: " (-> @lazy-ns-vars-a keys sort) ; KEYS identical to lazy-ns-loadable
                ;"\nlazy-ns-loadable FULL: "  @lazy-ns-loadable-a)  
                )))
(defn load-namespace-raw
  "returns a promise containing the resolved loadables for a namespace"
  [ns-name]
  ;(println "lazy-ns-loadable-keys: " (keys @lazy-ns-loadable-a) "ns: " ns-name)
  (let [loadable  (get @lazy-ns-loadable-a ns-name)
        rp (p/deferred)
        on-error (fn [err]
                   ;(println "could not load ns: " ns-name " ERROR: " err)
                   (p/reject! rp err))
        on-success (fn [vars]
                     ;(println "ns [" ns-name "] loaded successfully!")
                     (p/resolve! rp vars))]
    ; lazy/load does return a google deferred, so we cannot use promises here.
    (try
      (shadow.lazy/load loadable on-success on-error)
      (catch :default ex
        (println "shadow.lazy/load could not load ns: " ns-name "error: " ex)
        (p/reject! rp ex)))
    rp))

(defn- ns-assemble [ns-vars vars]
  (->> (map (fn [n v]
              [n v]) ns-vars vars)
       (into {})))

(defn simple-namespace? [ns-name]
  (let [ns-vars (get @lazy-ns-vars-a ns-name)]
    ;(println "simple-namespace? " ns-name "type: " (type ns-vars) "vector?" (vector? ns-vars))
    (vector? ns-vars)))

(defn assemble-simple-ns [ns-name vars]
  (let [ns-vars (get @lazy-ns-vars-a ns-name)]
    (ns-assemble ns-vars vars)))

(defn load-namespace
  "returns a promise containing 
   a map. keys = ns publics, values = vars"
  [ns-name]
  (let [rp (p/deferred)]
    (if (simple-namespace? ns-name)
      (let [rp2 (load-namespace-raw ns-name)]
        (-> rp2
            (p/then (fn [vars]
                      ;(println "load-namespace vars successfully received!")
                      (p/resolve! rp (assemble-simple-ns ns-name vars))))))
      (do (println "cannot load-namespace [" (pr-str ns-name) "] - not a simple-namespace!")
          (p/reject! rp (str "cannot load-namespace: "
                             ns-name " - is not a simple-namespace"))))
    rp))

(defn webly-resolve [fq-symbol]
  ;(println "resolving: " fq-symbol)
  (let [rp (p/deferred)
        ns-symbol (-> fq-symbol namespace symbol)
        fn-symbol (-> fq-symbol name symbol)
        ns-rp (load-namespace ns-symbol)]
    (-> ns-rp
        (p/then (fn [ns-map]
                  (if-let [fun (get ns-map fn-symbol)]
                    (do ;(println "resolved successfully: " fq-symbol)
                      (p/resolve! rp fun))
                    (do (println "could not resolve: " fq-symbol " in namespace: " ns-symbol)
                        (p/reject! rp (str "namespace does not contain function: " fn-symbol))))))
        (p/catch (fn [err]
                   (println "error in resolving " fq-symbol ": namespace not found: " ns-symbol " error: " err)
                   (p/reject! rp (str "namespace could not get loaded: " ns-symbol)))))
    rp))
