(ns webly.ws.core
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [webly.ws.adapter :refer [ws-init! start-router!]]
   [webly.ws.ws :as ws]))

(defonce c (atom nil))

(defn init-ws! [path port]
  (let [conn (ws-init! path port)]
    (reset! c conn)
    (start-router! conn)))

(defn send! [data]
  (info "sending: " data)
  (when data
    (ws/send @c data)))

(reg-event-db
 :ws/send
 (fn [db [_ data]]
   (debug "ws send: " data)
   (send! data)
   (debug "ws data sent!")
   db))

