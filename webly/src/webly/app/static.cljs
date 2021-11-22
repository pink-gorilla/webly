(ns webly.app.static
  (:require
   [taoensso.timbre :refer-macros [info warn]]))

(println "webly static app!")

(defn static-page []
  [:div
   [:h1 "I am a static page."]])

(defn ^:export start []
  (enable-console-print!)
  (println "webly static starting..")
  (info "webly static starting..")
  ;(webly-run!)
  )