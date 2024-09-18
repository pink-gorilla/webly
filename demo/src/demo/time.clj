(ns demo.time
  (:require
   [taoensso.timbre :as log :refer [info warn error]]
   [modular.date :refer [now-str]]
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
          (send-all! this [:demo/time (now-str)])
          (Thread/sleep 1000))
        (if @running?
          (recur)
          (info "time sender is stopping.."))))
    running?))

