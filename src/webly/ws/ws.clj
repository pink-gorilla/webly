(ns webly.ws.ws
  (:require
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [clojure.java.io :as io]
   [ring.middleware.keyword-params]
   [ring.middleware.params]
   [ring.util.response :as response]))

(defn send-all!
  [conn data]
  (let [{:keys [connected-uids chsk-send!]} conn
        uids (:any @connected-uids)
        nr (count uids)]
    (when (> nr 0)
      (debugf "Broadcasting event type: %s to %s clients" (first data) nr)
      (doseq [uid uids]
        (when-not (= uid :sente/nil-uid)
          (chsk-send! uid data))))))


;; helper fns


#_(defn send-ws-response [{:as ev-msg :keys [id ?data ring-req ?reply-fn send-fn]}
                          goldly-tag
                          response]
    (let [session (:session ring-req)
          uid (:uid session)]
      (when (nil? ?reply-fn)
        (error "reply-fn is nil. this should not happen."))
      (if (nil? uid)
        (error "uid is nil. this should not happen.")
        (info "uid: " uid))
      (if response
        (cond
          ?reply-fn (?reply-fn response)
          uid (chsk-send! uid [:goldly/system response])
          :else (error "Cannot send ws-response: neither ?reply-fn nor uid was set!"))
        (error "Can not send ws-response for nil response. " goldly-tag))))


