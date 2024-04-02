(ns webly.spa.service.timbre
  (:require
   [modular.log]))

(defn timbre-config! [config]
  (println "timbre config: " config)
  (modular.log/timbre-config! config))
