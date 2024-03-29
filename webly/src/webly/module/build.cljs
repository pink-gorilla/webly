(ns webly.module.build
  (:require-macros 
    [webly.module.build :refer [define-module get-lazy-modules]])
  (:require
    [shadow.lazy]))

(defonce lazy-modules-a (atom {}))
; key: module-name
; val: false/true (is-loaded?)

(defn add-module [module-name]
   (swap! lazy-modules-a assoc module-name false))

(defn add-lazy-modules 
  ;"adds modules to the build. Needs to be called from the cljs-app."
  []
  (let [modules (get-lazy-modules)]
    (doall (map add-module modules))))

(defn load-module [module-name]
  (shadow.lazy/load (get @lazy-modules-a module-name)))


; (define-module module-name)