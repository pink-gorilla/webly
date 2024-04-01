(ns webly.app.mode
  (:require-macros 
   [webly.build.prefs :refer [get-pref]])
  (:require 
   [taoensso.timbre :refer-macros [info warn]]
   [webly.app.mode.url :refer [application-url app-load-path]]
   [shadow.loader :as shadow-loader]
   [webly.app.mode.shadow-load :refer [set-shadow-load-dir]]))

;; mode

(defonce mode-a (atom :dynamic))

(defn get-mode []
  @mode-a)

;; resource-path

(defonce resource-path-a (atom (get-pref :asset-path)))

(defn set-resource-path [rp]
  (reset! resource-path-a rp)
 ; (set-shadow-load-dir rp "/index_files/")
 ; (shadow-loader/init "/index_files/") ; prefix to the path loader
  
  )

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
  (if (= mode "static")
    (let [path (application-url)
          default-resources "r/"
          ;resource-path (str path "/r")
          ;resource-path "./index.html_files/"
          resource-path (str path default-resources)
          ;resource-path  "./index_files/"
          ] ; relative load path  
      (reset! mode-a :static)
      (info "static mode app-url: " path " resource-path:" resource-path)  
      (shadow-loader/init "")
      (set-resource-path resource-path))
    (shadow-loader/init "/r/")
    ))

