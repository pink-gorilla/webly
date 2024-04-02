(ns webly.app.mode
  (:require-macros
   [webly.build.prefs :refer [get-pref]])
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [webly.app.mode.url :refer [current-path entry-path]]
   [shadow.loader :as shadow-loader]))

;; mode

(defonce mode-a (atom :dynamic))

(defn get-mode []
  @mode-a)

;; resource path

(defonce resource-path-a (atom (get-pref :asset-path)))

(defn get-resource-path []
  @resource-path-a)

;; routing path

(defonce routing-path-a (atom "/"))

(defn get-routing-path []
  @routing-path-a)

; resource-path “/r” (ex prefix)
; frontend-config
; Shadow-cljs module loader
; Css-loader
; Sci-ns-loader

;; service

(defn set-mode! [mode]
  (if (= mode "static")
    (let [cpath (current-path)
          epath (entry-path)
          resource-path (str epath "r/")]
      (reset! mode-a :static)
      (info "static mode: routing-path:" cpath " resource-path:" resource-path)
      (reset! routing-path-a cpath)
      (reset! resource-path-a resource-path)
      (shadow-loader/init epath))
    (let [resource-path "/r/"]
      (reset! resource-path-a resource-path)
      (info "dynamic mode: routing-path:" (get-routing-path) " resource-path:" (get-resource-path))
      (shadow-loader/init ""))))

