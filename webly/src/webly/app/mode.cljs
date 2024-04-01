(ns webly.app.mode
  (:require-macros 
   [webly.build.prefs :refer [get-pref]])
  (:require 
   [taoensso.timbre :refer-macros [info warn]]
   [webly.app.mode.url :refer [application-url app-load-path]]
   [webly.app.mode.shadow-load :refer [set-shadow-load-dir]]))

;; mode

(defonce mode-a (atom :dynamic))

(defn get-mode []
  @mode-a)

;; resource-path

(defonce resource-path-a (atom (get-pref :asset-path)))

(defn set-resource-path [rp]
  (reset! resource-path-a rp)
  (set-shadow-load-dir rp))

(defn get-resource-path []
  @resource-path-a)

; resource-path “/r” (ex prefix)
; frontend-config
; Shadow-cljs module loader
; Css-loader
; Sci-ns-loader

;; routing

(defonce route-base (atom "/"))

;; service

(defn set-mode! [mode]
  (warn "webly starting in mode: " mode)  
  (when (= mode "static")
    (warn "static mode!!")  
    (reset! mode-a :static)
    (let [path (application-url)
          ;resource-path (str path "/r")
          ;resource-path "./index.html_files/"
          resource-path (str (application-url) "index_files/")
          ;resource-path  "./index_files/"
          ] ; relative load path  
      (warn "app url: " path)
      (warn "resource-path: " resource-path)
      (set-resource-path resource-path))))

