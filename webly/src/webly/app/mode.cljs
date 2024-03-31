(ns webly.app.mode
  (:require-macros 
   [webly.build.prefs :refer [get-pref]])
  (:require 
   [webly.app.mode.shadow-loader :refer [shadow-load-hack]]))

(defonce mode-a (atom :dynamic))

(defonce resource-path-a (atom (get-pref :asset-path)))

(defonce route-base (atom "/"))


(defn set-resource-path [rp]
  (reset! resource-path-a rp)
  (shadow-load-hack rp))

(defn get-resource-path []
  @resource-path-a)

; resource-path “/r” (ex prefix)
; frontend-config
; Shadow-cljs module loader
; Css-loader
; Sci-ns-loader
