(ns webly.module.build
  (:require-macros 
    [webly.module.build :refer [get-lazy-modules get-lazy-ns define-lazy-ns ]])
  (:require
    [shadow.lazy]))

(defonce lazy-modules-a (atom {}))
; key: module-name
; val: false/true (is-loaded?)

(defonce lazy-ns-a (atom {}))


(defn module-list->map [module-list]
  (->> (map (fn [n] [n false]) module-list)
       (into {})))

(defn add-lazy-modules 
  ;"adds modules to the build. Needs to be called from the cljs-app."
  []
  (let [modules (get-lazy-modules)
        modules-map (module-list->map modules)
        ns-loadables (get-lazy-ns)]
    (reset! lazy-modules-a modules-map)
    (reset! lazy-ns-a ns-loadables)
    :ok))

(defn load-module [module-name]
  (shadow.lazy/load (get @lazy-modules-a module-name)))


; (define-module module-name)