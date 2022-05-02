(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warn error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [modular.date :refer [now-str now  now-local]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]))

(defn stop! [a]
  (warn "stopping demo time sender!")
  (reset! a false))

(defn start-time-sender!
  []
  (info "starting time sender..")
  (let [running? (atom true)]
    (go-loop []
      (<! (async/timeout 5000))
      (send-all! [:demo/time (now-str)])
      (if @running? 
        (recur)  
        (info "time sender is stopping.."))
      )
     running?))

(defmethod -event-msg-handler :time/now
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time (now-str)))

(defmethod -event-msg-handler :time/now-date
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time-date (now)))

(defmethod -event-msg-handler :time/now-date-local
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time-date-local (now-local)))


(defmethod -event-msg-handler :demo/connected
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":ws/status: %s" event)
  (let [c (connected-uids)]
    (info "connected uids: " c)
    (send-response req  :demo/connected c)))

