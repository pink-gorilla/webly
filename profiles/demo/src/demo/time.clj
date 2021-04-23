(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [webly.date :refer [now-str]]
   [webly.ws.core :refer [send-all!]]))

(defn start-time-sender!
  []
  (go-loop []
    (<! (async/timeout 5000))
    (send-all! [:demo/time (now-str)])
    (recur)))
