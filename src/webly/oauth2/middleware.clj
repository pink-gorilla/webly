(ns webly.oauth2.middleware
  (:require
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring-ttl-session.core :refer [ttl-memory-store]]
   [ring.middleware.session :refer [wrap-session]]
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.oauth2 :refer [wrap-oauth2]]
   [webly.user.secrets.core :refer [secrets]]))

(defn oauth2-from-secrets []
  (let [s (secrets)
        {:keys [github name]} s
        {:keys [client-id client-secret]} github]
    (println "oauth2 profile:" name)
    (when (and client-id client-secret)
      {:github
       {:authorize-uri    "https://github.com/login/oauth/authorize"
        :access-token-uri "https://github.com/login/oauth/access_token"
        :client-id        client-id
        :client-secret    client-secret
        :scopes           ["user:email" "gist"]
        :launch-uri       "/oauth2/github/auth"
        :redirect-uri     "/oauth2/github/callback"
        :landing-uri      "/oauth2/github/landing"}})))

(let [p (oauth2-from-secrets)]
  (if p
    (def my-oauth-profiles p)
    (def my-oauth-profiles {})))

  ; https://github.com/weavejester/ring-oauth2 

(defn wrap-oauth [handler]
  ;(wrap-oauth2 handler my-oauth-profiles)
  (-> handler
      (wrap-oauth2 my-oauth-profiles)
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
            ;(assoc-in [:session :store] (ttl-memory-store (* 60 30)))
           (assoc-in [:session :cookie-attrs :same-site] :lax)))))

; wrap-oauth2 needs to be the first position!
; (defn wrap-base [handler]
;  

