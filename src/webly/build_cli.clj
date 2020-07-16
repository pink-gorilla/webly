(ns webly.build-cli
  (:require
   [webly.build :refer [build-cli]]))

(defn -main
  [mode lein-profile handler frontend-ns]
  (println "webly.build-cli -main" mode lein-profile handler frontend-ns)
  (build-cli mode lein-profile handler frontend-ns))