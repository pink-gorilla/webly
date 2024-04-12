(ns webly.spa.mode
  (:require-macros
   [webly.build.prefs :refer [get-pref]])
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer-macros [info warn]]
   [webly.spa.mode.url :refer [current-path entry-path entry-path-full]]
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
          epath-full (entry-path-full)
          resource-path (if (str/ends-with? epath "/")
                            (str epath "r/")
                            (str epath "/r/"))]
      (reset! mode-a :static)
      (info "static mode: routing-path:" cpath " resource-path:" resource-path "entry-path: " epath)
      (info "full-entry-path: " epath-full)
      (reset! routing-path-a cpath)
      (reset! resource-path-a resource-path)
      (shadow-loader/init epath-full))
    (let [resource-path "/r/"]
      (reset! resource-path-a resource-path)
      (info "dynamic mode: routing-path:" (get-routing-path) " resource-path:" (get-resource-path))
      (shadow-loader/init "")))) 