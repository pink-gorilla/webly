(ns modular.ws.core
  (:require
   [taoensso.timbre :as log :refer [error info warn]]
   [modular.ws.adapter :refer [ws-init! start-router!]]
   [modular.ws.handler :refer [add-ws-handler]]
   [modular.ws.ws :as ws]))

(def c (atom nil))

(def watcher-cbs (atom []))

(defn- watch-conn-start [conn]
  (let [{:keys [connected-uids]} conn]
    (add-watch connected-uids :connected-uids
               (fn [_ _ old new]
                 (when (not= old new)
                   (doall (for [cb @watcher-cbs]
                            (cb old new))))))))

(defn watch-conn [cb]
  (swap! watcher-cbs conj cb))

(defn- log-conn-chg [old new]
  (info "conn chg: old:" old "new: " new))

(defn init-ws! [server-type]
  (let [conn (ws-init! server-type)]
    (reset! c conn)
    (watch-conn log-conn-chg)
    (watch-conn-start conn)
    (start-router! conn)
    (add-ws-handler conn)))

(defn send! [uid data]
  (if @c
    (ws/send! @c uid data)
    (error "ws/send - not setup. data: " data)))

(defn send-all! [data]
  (if @c
    (ws/send-all! @c data)
    (error "ws/send-all - not setup. data: " data)))

(defn send-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn uid send-fn]}
                     msg-type response]
  ;(let [session (:session ring-req)
        ;uid (:uid session)
   ;     ]
   ; (when (nil? ?reply-fn)
   ;   (warn "reply-fn is nil. the client did chose to use messenging communication istead of req-res communication."))
    ;(warn "ws/session: " session)
    ;(if (nil? uid)
    ;  (warn "ws request uid is nil. ring-session not configured correctly.")
    ;  (info "ws/uid: " uid))
  (if (and msg-type response)
    (cond
      ?reply-fn (?reply-fn [msg-type response])
      uid (send! uid [msg-type response])
      :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
    (error "Can not send ws-response - msg-type and response have to be set, msg-type:" msg-type "response: " response)))

(defn connected-uids []
  (let [{:keys [connected-uids]} @c
        uids (:any @connected-uids)]
    uids))

(comment
  ;(println "clients: " @connected-uids)
  (send-all! [:demo/broadcast {:a 13}])

  ;
  )