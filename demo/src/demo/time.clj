(ns demo.time
  (:require
   [taoensso.timbre :refer [info warn error]]
   [tick.core :as t]
   [modular.ws.core :refer [send-all!]]))

; TIME PUSHER

(defn stop! [this]
  (warn "stopping demo time sender!")
  (reset! this false))

(defn start-time-sender!
  [this]
  (info "starting time sender..")
  (let [running? (atom true)]
    (future
      (loop []
        (when  @running?
          (send-all! this [:demo/time (str (t/instant))])
          ;(send-all! this [:demo/time2 (t/instant)])
          (send-all! this [:demo/time2 (t/instant)])
          (Thread/sleep 1000))
        (if @running?
          (recur)
          (info "time sender is stopping.."))))
    running?))


