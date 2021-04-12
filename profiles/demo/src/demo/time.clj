(ns demo.time
  (:import java.util.Date)
  (:import java.text.SimpleDateFormat)
  (:import java.text.ParseException)
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [webly.ws.core :refer [send-all!]]))


(defn now [] (java.util.Date.))

(defn tostring [dt]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") dt))


(defn start-time-sender!
  []
  (go-loop []
    (<! (async/timeout 5000))
    (send-all! [:demo/time (tostring (now))])
    (recur)))
