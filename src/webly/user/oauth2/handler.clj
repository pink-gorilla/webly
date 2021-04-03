(ns webly.user.oauth2.handler
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [ring.util.response :as res]
   [ajax.core :as ajax]
   [webly.config :refer [get-in-config]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.user.oauth2.middleware :refer [wrap-oauth wrapx]]))

(defn handler-auth [request]
  ; Once the user is authenticated, a new key is added to every request:
  ;   :oauth2/access-tokens
  (println "oauth2 tokens: " (-> request :oauth2/access-tokens :github))
  (let [github-token (:token (-> request :oauth2/access-tokens :github))
        _ (println "github token: " github-token)]
    ;(println (tentacles.gists/user-gists "awb99" {:oauth-token github-token}))
    (res/response {:token github-token})))

(def handler-auth-wrapped
  (-> handler-auth
      wrap-oauth))

(add-ring-handler :webly/oauth2 handler-auth-wrapped)

(defn handler-github-redirect [req]
  (let [p (promise)
        {:keys [clientId clientSecret]} (get-in-config [:oauth2 :github])
        code (get-in req [:params :code])]

    (println "getting access token for code : " code " clientId: " clientId)
    (ajax/POST "https://github.com/login/oauth/access_token"
      :params {:client_id	 clientId
               :client_secret clientSecret
               :code code}
      :format (ajax/json-request-format);  {:keywords? true})
      :timeout 25000                     ;; optional see API docs
      :response-format (ajax/json-response-format); {:keywords? true})
      :handler (fn [res]
                 (println "github auth: " res)
                 (deliver p res))
      :error-handler (fn [res]
                       (println "github auth error: " res)
                       (deliver p res)))

    (merge {:body "hi"} @p)))

(comment
  (handler-github-redirect {})

  ;
  )

(def handler-github-redirect-wrapped
  (-> handler-github-redirect
      wrapx))

(add-ring-handler :webly/oauth2-github handler-github-redirect-wrapped)