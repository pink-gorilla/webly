(ns modular.oauth2.local.handler
  (:require
   [cheshire.core :as json]
   [taoensso.timbre :refer [debug debugf info infof error]]
   [clj-jwt.core :refer [str->jwt]]
   [modular.config :refer [get-in-config]]
   [modular.ws.core :refer [send-all! send-response connected-uids]]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [modular.oauth2.local.pass :refer [get-token]]
   [modular.permission.user :refer [find-user-id-via-email]]
   [modular.permission.websocket :refer [set-user!]]
   [modular.oauth2.token.info :refer [tokens-summary-map]]))

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

(defmethod -event-msg-handler :login/local
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn uid]}]
  (debugf ":login/local %s" ?data)
  (let [{:keys [username password]} ?data
        login-result (get-token username password)]
    (info "login/local: " login-result " uid: " uid)
    (when-let [user (:user login-result)]
      (set-user! uid user))
    (send-response req :login/local login-result)))

(defmethod -event-msg-handler :login/oidc
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn uid]}]
  (debugf ":login/oidc %s" ?data)
  (let [{:keys [id-token]} ?data
        login-result (str->jwt id-token)
        email (get-in login-result [:claims :email])]
    (debug "login result oidc: " login-result " uid: " uid)
    (infof "login/oidc email: %s uid: %s" email uid)
    (if-let [user (find-user-id-via-email email)]
      (let [user (name user)]
        (set-user! uid user)
        (send-response req
                       :login/local {:user user :token ?data}))
      (do
        (error "no user found for oidc login with email: " email)
        (send-response req
                       :login/local {:error :user-not-found
                                     :error-message (str "No user found for [" email "].")})))))

(defmethod -event-msg-handler :tokens/summary
  [{:as req :keys [event id ?data ring-req ?reply-fn send-fn uid]}]
  (debugf ":tokens/summary %s" ?data)
  (let [{:keys [providers]} ?data
        summary (tokens-summary-map providers)]
    (infof ":tokens providers: %s summary: %s" providers summary)
    (send-response req :tokens/summary summary)))
