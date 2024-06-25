(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warn error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [modular.date :refer [now-str now  now-local]]
   [modular.ws.core :refer [send-all!]]
   [modular.ws.msg-handler :refer [-event-msg-handler send-response]]))

; EVENTHANDLER

(defmethod -event-msg-handler :time/now
  [{:as req :keys [event _id _?data _ring-req _?reply-fn _send-fn]}]
  (infof ":time/now: %s" event)
  (let [stime (now-str)]
    (info "sending time: " stime)
    (send-response req :demo/time stime)))

(defmethod -event-msg-handler :time/now-date
  [{:as req :keys [event _id _?data _ring-req _?reply-fn _send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time-date (now)))

(defmethod -event-msg-handler :time/now-date-local
  [{:as req :keys [event _id _?data _ring-req _?reply-fn _send-fn]}]
  (infof ":time/now: %s" event)
  (send-response req :demo/time-date-local (now-local)))

; TIME PUSHER

(defn stop! [this]
  (warn "stopping demo time sender!")
  (reset! this false))

(defn start-time-sender!
  [this]
  (info "starting time sender..")
  (let [running? (atom true)]
    (go-loop []
      (<! (async/timeout 5000))
      (when  @running?
        (send-all! this [:demo/time (now-str)]))
      (if @running?
        (recur)
        (info "time sender is stopping..")))
    running?))


