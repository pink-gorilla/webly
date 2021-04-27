(ns webly.ws.core
  (:require
   [taoensso.timbre :as log :refer [error info warn]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [webly.ws.adapter :refer [ws-init! start-router!]]
   [webly.ws.handler :refer [add-ws-handler]]
   [webly.ws.ws :as ws]))

(def c (atom nil))

(defn log-on-conn-chg [old new]
  (info "conn chg: old:" old "new: " new))

(def on-conn-chg (atom log-on-conn-chg))

(defn- watch-conn [conn]
  (let [{:keys [connected-uids]} conn]
    (add-watch connected-uids :connected-uids
               (fn [_ _ old new]
                 (when (not= old new)
                   (let [notify @on-conn-chg]
                     (notify old new)))))))

(defn init-ws! [server-type]
  (let [conn (ws-init! server-type)]
    (watch-conn conn)
    (start-router! conn)
    (add-ws-handler conn)
    (reset! c conn)))

(defn send! [uid data]
  (if @c
    (ws/send! @c uid data)
    (error "ws/send - not setup. data: " data)))

(defn send-all! [data]
  (if @c
    (ws/send-all! @c data)
    (error "ws/send-all - not setup. data: " data)))

(defn send-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn send-fn]}
                     msg-type
                     response]
  (let [session (:session ring-req)
        uid (:uid session)]
    (when (nil? ?reply-fn)
      (error "reply-fn is nil. the client did chose to use messenging communication istead of req-res communication."))
    (if (nil? uid)
      (warn "ws request uid is nil. ring-session not configured correctly.")
      (info "ws/uid: " uid))
    (if response
      (cond
        ?reply-fn (?reply-fn [msg-type response])
        uid (send! uid [msg-type response])
        :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
      (error "Can not send ws-response for nil response. " msg-type))))

(comment
  ;(println "clients: " @connected-uids)

  (send-all! [:pinkie/broadcast {:a 13}])

  ;
  )