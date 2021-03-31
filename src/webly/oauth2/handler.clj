(ns webly.oauth2.handler
  (:require
   [ring.util.response :as res]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.oauth2.middleware :refer [wrap-oauth]]))


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