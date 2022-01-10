(ns modular.oauth2.local.handler
  (:require
   [cheshire.core :as json]
   [taoensso.timbre :refer [info infof error]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.oauth2.local.pass :refer [get-token]]
   ))

(defn login-handler
 "Login endpoint.. Returns token
  not used (we use websocket login)"
  [request]
  (let [data (:form-params request)
        {:keys [username password]} data
        login-result (get-token username password)]
    {:status 200
     :body (json/encode login-result)
     :headers {:content-type "application/json"}}))


(def connected-users (atom {}))

(defn set-user! [uid user]
  (info "ws uid: " uid " has user: " user)
  (swap! connected-users assoc (keyword uid) user)
  (info "connected-users: " (pr-str @connected-users)))



(defmethod -event-msg-handler :login/local
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn uid]}]
  (infof ":login/local %s" ?data)
  (let [{:keys [username password]} ?data
        login-result (get-token username password)]
    (info "login result: " login-result " uid: " uid)
    (when-let [user (:user login-result)] 
      (set-user! uid user))
    (send-response req :login/local login-result)
    ))