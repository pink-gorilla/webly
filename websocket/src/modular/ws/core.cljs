(ns modular.ws.core
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :as rf]
   [modular.ws.adapter :refer [ws-init! start-router!]]
   [modular.ws.ws :as ws]))

(defonce c (atom nil))

(defn init-ws! [path port]
  (let [conn (ws-init! path port)]
    (reset! c conn)
    (start-router! conn)))

(defn send!
  ([data]
   (when data
     (info "sending (no cb): " data)
     (try
       (ws/send @c data)
       (catch :default e
         (error "exception sending to ws: " e)))))
  ([data cb timeout]
   (when data
     (info "sending (cb): " data)
     (try
       (ws/send @c data cb timeout)
       (catch :default e
         (error "exception sending to ws: " e))))))

(rf/reg-event-db
 :ws/send
 (fn [db v]
   (case (count v)
     2 (let [[_ data] v]
         (info "ws send (no cb): " data)
         (send! data))
     4 (let [[_ data cb timeout] v]
         (info "ws send (cb): " data)
         (send! data cb timeout))
     (error ":ws/send bad format: " v))
   db))