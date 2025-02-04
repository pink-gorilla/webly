(ns shadowx.build.lazy
  (:require-macros [shadowx.build.lazy])
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [frontend.css :refer [loading?]]
   [shadow.lazy :as lazy]))

; https://code.thheller.com/blog/shadow-cljs/2019/03/03/code-splitting-clojurescript.html
; https://clojureverse.org/t/shadow-lazy-convenience-wrapper-for-shadow-loader-cljs-loader/3841

(defonce renderer (r/atom {}))

(defn available []
  (keys @renderer))

(defn add-available [s]
  (debug "available lazy renderer: " s)
  (swap! renderer assoc s {:symbol s}))

(defn add-loaded [s]
  ;(println "add-loaded: " s)
  (fn [f]
    ;(println "add-loaded resolved: " f)
    (swap! renderer assoc-in [s :fun] f)
    ;(println "renderer: " (pr-str @renderer))
    ))

(defn on-error [err]
  (error "could not load ns:  ERROR: " err))

(defn start-load [symbol-fn load-spec]
  ;(println "start loading: " symbol-fn " load-spec: " load-spec)
  (try
    (lazy/load load-spec (add-loaded symbol-fn) on-error)
    (catch :default e
      (error "Lazy Loading failed for: " symbol-fn " error: " e))
    nil))

(defn run [f args]
  (if args
      (apply f args)
      (f)))

(defn loading-status [symbol-fn]
  [:span "lazy-load "])

(defn show-lazy [load-spec symbol-fn]
  (fn [& args]
    (let [r (get-in @renderer [symbol-fn :fun])]
      (if r ; (and r (not @loading?))
        [run r args]
        (do (.setTimeout js/window #(start-load symbol-fn load-spec) 1)
            [loading-status symbol-fn]
            )))))



