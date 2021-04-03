(ns webly.log
  (:require
   [taoensso.timbre :as timbre]))

(defn timbre-config! [config]
  (let [timbre-loglevel (:timbre-loglevel config)]
    (println "timbre config: " timbre-loglevel)
    (when timbre-loglevel
      (timbre/set-config!
       (merge timbre/default-config
              {:min-level timbre-loglevel})))))
