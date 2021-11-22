(ns modular.ws.msg-handler
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debug debugf infof info  warnf error errorf trace]]
   [re-frame.core :as rf]
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
    (rf/dispatch [:ws/state new-state-map old-state-map])))

;; This is the main event handler; If we want to do cool things with other kinds of data 
;; going back and forth, this is where we'll inject it.

(defmethod -event-msg-handler :chsk/ws-ping
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":chsk/ws-ping: %s" event))

(defmethod -event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (if (= ?data [:chsk/ws-ping])
    (debug "ws-ping rcvd.")
    (if (vector? ?data)
      (do ;(info "dispatching rcvd ws msg to reframe:" (first ?data))
        (debug "dispatching rcvd ws msg to reframe:" ?data)
        (rf/dispatch ?data))
      (error "ws rcvd. cannot dispatch. data no vector: " ?data))))

(defmethod -event-msg-handler :default
  [{:as ev-msg :keys [event]}]
  (errorf "Unhandled ws event: %s" event))

; msg-handler

(defn event-msg-handler
  "Wraps `-event-msg-handler` with logging, error catching, etc."
  [{:keys [id ?data event] :as req}]
  (debugf "ws rcvd: evt: %s id: %s data: %s" event id ?data)
  (-event-msg-handler req))