(ns webly.init
  (:require
   [shadowx.module.build :refer [add-lazy-modules print-build-summary webly-resolve]]
   [webly.spa.mode :refer [set-mode!]]))

(add-lazy-modules)
(print-build-summary)

(defn ^:export start [mode]
  (set-mode! mode)
  (let [start-s 'webly.spa/start
        start-fn-p (webly-resolve start-s)]
    (println "starting: " start-s " mode: " mode)
    (.then start-fn-p (fn [start-fn]
                        (start-fn mode)))))