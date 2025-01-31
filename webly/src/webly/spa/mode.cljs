(ns webly.spa.mode
  (:require-macros
   [shadowx.build.prefs :refer [get-pref]])
  (:require
   ;[clojure.string :as str]
   [webly.spa.mode.url :refer [current-path entry-path entry-path-full]]
   [shadow.loader :as shadow-loader]))

; we don't want to move clojure.string to :bootstrap(:init) bundle.
; so we re-implement some features we need here.

(defn ends-with? [s e]
  (.endsWith s e))

(defn str2 [s1 s2]
  (.concat s1 s2))

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
          resource-path (if (ends-with? epath "/")
                          (str2 epath "r/")
                          (str2 epath "/r/"))]
      (reset! mode-a :static)
      (println "static mode: routing-path:" cpath " resource-path:" resource-path "entry-path: " epath)
      (println "full-entry-path: " epath-full)
      (reset! routing-path-a cpath)
      (reset! resource-path-a resource-path)
      (shadow-loader/init epath-full))
    (let [resource-path "/r/"]
      (reset! resource-path-a resource-path)
      (println "dynamic mode: routing-path:" (get-routing-path) " resource-path:" (get-resource-path))
      (shadow-loader/init ""))))