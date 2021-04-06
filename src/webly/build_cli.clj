(ns webly.build-cli
  (:require
   [webly.build :refer [build-cli]]))

(defn -main
  [mode build-id]
  (println "webly.build-cli -main" mode build-id)
  (build-cli mode build-id))