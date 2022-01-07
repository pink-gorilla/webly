(ns oauth2.local.handler
  (:require
   [buddy.sign.jwt :as jwt]))

(defn handler-local-login
  "Login endpoint.. Returns token"
  [req])

(defn login-handler
  [request]
  (let [data (:form-params request)
        user (find-user (:username data)   ;; (implementation ommited)
                        (:password data))
        token (jwt/sign {:user (:id user)} secret)]
    {:status 200
     :body (json/encode {:token token})
     :headers {:content-type "application/json"}}))
