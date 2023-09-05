(ns modular.ws.msg-handler
  (:require
   [taoensso.timbre :refer [tracef debug debugf info infof warn warnf error errorf]]
   [modular.permission.app :refer [service-authorized?]]))

(defn ws-reply [{:keys [event id ?data ring-req ?reply-fn send-fn] :as req}
                res]
  (when ?reply-fn
    (?reply-fn res)))

(defn send-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn uid send-fn]}
                     msg-type response]
  ;(let [session (:session ring-req)
        ;uid (:uid session)
   ;     ]
  ;(when (nil? ?reply-fn)
   ; (warn "reply-fn is nil. the client did chose to use messenging communication istead of req-res communication."))
    ;(warn "ws/session: " session)
    ;(if (nil? uid)
    ;  (warn "ws request uid is nil. ring-session not configured correctly.")
    ;  (info "ws/uid: " uid))
  (if (and msg-type response)
    (cond
      ?reply-fn (?reply-fn [msg-type response])
      uid (send-fn uid [msg-type response])
      :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
    (error "Can not send ws-response - msg-type and response have to be set, msg-type:" msg-type "response: " response)))

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
    (send-response req :ws/unknown event)))

(def always-authorized
  #{:chsk/uidport-open
    :chsk/uidport-close
    :chsk/ws-ping
    :chsk/handshake
    :chsk/recv
    :login/local
    :login/oidc
    :tokens/summary})

(defn is-authorized? [msg-type uid]
  (if (contains? always-authorized msg-type)
    true
    (service-authorized? msg-type uid)))

(defn send-reject-response [req msg-type]
  (send-response req
                 msg-type
                 {:error "Not Authorized"
                  :error-message "You are not authorized for this service"}))

(defn event-msg-handler [{:keys [client-id id event ?data uid] :as req}]
  (debugf "ws rcvd: evt: %s id: %s data: %s" event id ?data)
  (when req
    (let [msg-type (first event)]
      (if (is-authorized? msg-type uid)
        (-event-msg-handler req)
        (send-reject-response req msg-type)))))

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

