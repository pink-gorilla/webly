(ns webly.ws.msg-handler
  (:require
   [taoensso.timbre :refer [tracef debugf info infof warnf error errorf]]))

(defn ws-reply [{:keys [event id ?data ring-req ?reply-fn send-fn] :as req}
                res]
  (when ?reply-fn
    (?reply-fn res)))

(defmulti -event-msg-handler :id)

(defmethod -event-msg-handler :chsk/uidport-open
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":chsk/uidport-open: %s" event))

(defmethod -event-msg-handler :chsk/uidport-close
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (infof ":chsk/uidport-close: %s" event))

(defmethod -event-msg-handler :chsk/ws-ping
  [{:as ev-msg :keys [event id ?data ring-req ?reply-fn send-fn]}]
  (debugf ":chsk/ws-ping: %s" event))

(defmethod -event-msg-handler :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn] :as req}]
  (let [session (:session ring-req)
        uid (:uid session)]
    (errorf "ws event of unknown type. Please implement (-event-handler %s) event: %s" id event)
    (ws-reply req [:ws/unknown event])))

(defn event-msg-handler [{:keys [client-id id event ?data] :as req}]
  (debugf "ws rcvd: evt: %s id: %s data: %s" event id ?data)
  (when req
    (-event-msg-handler req)))

; {:client-id "591b690d-5633-48c3-884d-348bbcf5c9ca"
; :uid "3c8e0a40-356c-4426-9391-1445140ff509"
; :event [:chsk/uidport-close "3c8e0a40-356c-4426-9391-1445140ff509"]
; :id :chsk/uidport-close 
; :?data "3c8e0a40-356c-4426-9391-1445140ff509"
; :?reply-fn nil, 
; :connected-uids #object[clojure.lang.Atom 0x76b6a6d1 
; {:status :ready, :val {:ws #{}, 
;                        :ajax #{"82c82316-9c01-4abf-8046-e6e676246468"}, 
;                        :any #{"82c82316-9c01-4abf-8046-e6e676246468"}}}], 

