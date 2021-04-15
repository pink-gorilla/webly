(ns webly.ws.ws
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf info infof warnf error errorf trace]]
   [cljs.core.async :as async  :refer [<! >! put! chan]]
   [re-frame.core :refer [dispatch]]))

(defn cb-dispatch-to-reframe
  [cb-reply]
  (debugf "dispatching ws callback reply: %s" cb-reply)
  (if (vector? cb-reply)
    (do
      (info "dispatching cb: " cb-reply)
      (dispatch cb-reply))
    (error "ws reply/dispatch to reframe failed. not a vector: " cb-reply)))

(defn send
  ([conn data]
   (send conn data cb-dispatch-to-reframe))
  ([conn data cb]
   (let [{:keys [chsk-send!]} conn]
     (info "chsk-send!")
     (if chsk-send!
       (chsk-send! data 5000 cb)  ; sente send callbacks dont work with reframe
       (error "chsk-send! not defined! " data)))))
