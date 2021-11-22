(ns modular.ws.ws
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info error]]
   [re-frame.core :as rf]))

(defn- cb-dispatch-to-reframe
  [cb-reply]
  (debugf "dispatching ws callback reply: %s" cb-reply)
  (if (vector? cb-reply)
    (do
      (debug "dispatching cb: " cb-reply)
      (rf/dispatch cb-reply))
    (error "ws reply/dispatch to reframe failed. not a vector: " cb-reply)))

(defn send
  ([conn data]
   (let [{:keys [chsk-send!]} conn]
     (debug "chsk-send!")
     (if chsk-send!
       (chsk-send! data)  ; sente send callbacks dont work with reframe
       (error "chsk-send! not defined! cannot send: " data))))
  ([conn data cb timeout]
   (let [{:keys [chsk-send!]} conn]
     (debug "chsk-send!")
     (if chsk-send!
       (chsk-send! data timeout cb)  ; sente send callbacks dont work with reframe
       (error "chsk-send! not defined! cannot send: " data)))))
