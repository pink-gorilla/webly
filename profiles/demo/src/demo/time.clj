(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [webly.date :refer [now-str now]]
   [webly.ws.core :refer [send-all! send-response connected-uids]]
   [webly.ws.msg-handler :refer [-event-msg-handler]]))

(defn start-time-sender!
  []
  (info "starting time sender..")
  (go-loop []
    (<! (async/timeout 5000))
    (send-all! [:demo/time (now-str)])
    (recur)))

(defmethod -event-msg-handler :time/now
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time (now-str)))

(defmethod -event-msg-handler :ws/status
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":ws/status: %s" event)
  (let [c (connected-uids)]
    (info "connected uids: " c)
    (send-response req :ws/status c)))

(defmethod -event-msg-handler :time/now-date
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time-date (now)))