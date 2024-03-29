(ns webly.module.build
  (:require
   [extension :refer [get-extensions write-service]]))

;; lazy modules

(defonce lazy-modules-a (atom {}))

(defn- add-lazy-module [{:keys [name cljs-ns-bindings]}]
  (swap! lazy-modules-a assoc name cljs-ns-bindings))

(defmacro get-lazy-modules []
  (keys @lazy-modules-a))

(defmacro define-module [module-name]
  (let [m (get @lazy-modules-a module-name)]
    `(shadow.lazy/loadable ~m)))

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
    (reset! lazy-modules-a modules)))

