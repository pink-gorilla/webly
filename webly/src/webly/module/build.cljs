(ns webly.module.build
 (:require-macros [webly.module.build :refer [define-module get-modules])
  (:require
     [shadow.lazy :refer [loadable]])))

(defonce modules-a (atom {}))
; key: module-name
; val: loadable-spec)

(defn add-module [module-name ns-map]
   (swap! modules-a assoc module-name (define-module module-name))

(defn add-modules 
  “adds modules to the build. Needs to be called from the cljs-app.”
[]
  (let [modules (get-modules)]
    (doall (map add-module modules)))

(defn load-module [module-name]
  (shadow.lazy/load (get @modules-a module-name))

