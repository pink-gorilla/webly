(ns webly.ws.ws
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
   (send conn data cb-dispatch-to-reframe))
  ([conn data cb]
   (let [{:keys [chsk-send!]} conn]
     (debug "chsk-send!")
     (if chsk-send!
       (chsk-send! data 5000 cb)  ; sente send callbacks dont work with reframe
       (error "chsk-send! not defined! " data)))))
