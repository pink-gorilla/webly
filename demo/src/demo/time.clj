(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warn error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [modular.date :refer [now-str now  now-local]]
   [modular.ws.core :refer [send-all!]]))

; EVENTHANDLER

(defn time-now []
  (now-str))

(defn time-now-date []
  (now))

(defn time-now-date-local []
  (now-local))

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


