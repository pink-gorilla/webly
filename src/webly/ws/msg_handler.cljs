(ns webly.ws.msg-handler
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer [go go-loop]])
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf error errorf trace]]
   [cljs.core.async :as async  :refer [<! >! put! chan]]
   [re-frame.core :refer [reg-event-db dispatch-sync dispatch]]
   [taoensso.encore :as encore :refer-macros [have have?]]))

(defmulti -event-msg-handler :id)

(defmethod -event-msg-handler :chsk/handshake
  [{:as ev-msg :keys [?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    (debugf "Handshake: %s" ?data)))

(defmethod -event-msg-handler :chsk/state
  [{:keys [?data] :as ev-msg}]
  (let [[old-state-map new-state-map] (have vector? ?data)]
    (debugf "ws state: %s" new-state-map)
    (dispatch [:ws/state new-state-map old-state-map])))


;; This is the main event handler; If we want to do cool things with other kinds of data 
;; going back and forth, this is where we'll inject it.


(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (if (vector? ?data)
    (dispatch ?data)
    (error "ws rcvd. cannot dispatch. data no vector: " ?data)))

(defmethod -event-msg-handler :chsk/ws-ping
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (debugf ":chsk/ws-ping: %s" event))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (errorf "Unhandled ws event: %s" event))

; msg-handler

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:as ev-msg :keys [id ?data event]}]
  (debugf "ws rcvd: id: %s ?data: %s" id ?data)
  (-event-msg-handler ev-msg))