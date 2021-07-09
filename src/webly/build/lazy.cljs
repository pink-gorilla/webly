(ns webly.build.lazy
  (:require-macros [webly.build.lazy])
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [shadow.lazy :as lazy]))

; https://code.thheller.com/blog/shadow-cljs/2019/03/03/code-splitting-clojurescript.html
; https://clojureverse.org/t/shadow-lazy-convenience-wrapper-for-shadow-loader-cljs-loader/3841

(defonce renderer (r/atom {}))

(defn log-loading [symbol-fn]
  (error "lazy loading: " symbol-fn))

(defonce on-load (r/atom log-loading))

(defn available []
  (keys @renderer))

(defn add-available [s]
  (info "available lazy renderer: " s)
  (swap! renderer assoc s {:symbol s}))

(defn add-loaded [s f]
  (error "loaded lazy renderer: " s)
  (swap! renderer assoc-in [s :fun] f))

(defn start-load [symbol-fn load-spec]
  (@on-load)
  (lazy/load load-spec (partial add-loaded symbol-fn)))

(defn run [f args]
  (if args
    (apply f args)
    (f)))

(defn loading [& args]
  [:span "loading"])

(defn show-lazy [load-spec symbol-fn]
  (fn [& args]
    (let [r (get-in @renderer [symbol-fn :fun])]
      (if r
        [run r args]
        (do (start-load symbol-fn load-spec)
            [loading])))))



