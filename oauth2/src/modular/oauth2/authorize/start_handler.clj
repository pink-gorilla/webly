(ns modular.oauth2.authorize.start-handler
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [ring.util.response :as response]
   [modular.config :refer [get-in-config]]
   [modular.oauth2.provider :refer [url-authorize]]))

(defn handler-oauth2-start [req]
  ;(info "oauth2/authorize-start: " req)
  (info "oauth2/authorize-start: params:" (:params req))
  ;(info "oauth2/authorize-start: query-params:" (:query-params req))
  (let [{:keys [scheme server-name server-port uri]} req
        ; {:scheme :http :server-name "localhost" :server-port 8080
        ;  :uri "/api/oauth2/start/github" :protocol "HTTP/1.1"}
        ; :headers {"host" "localhost:8080"}, 
        oauth-config (get-in-config [])
        provider (get-in req [:route-params :provider])
        provider-kw (keyword provider)
        _  (info "oauth2 start for provider: " provider-kw)
        ;current-url (get-in req [:query-params :current-url])
        ;current-url "http://localhost:8080/"
        current-url (str (name scheme) "://" server-name ":" server-port uri)
        _ (debug "current url: " current-url)
        url-auth (url-authorize oauth-config provider-kw current-url)]
    (debug "url auth: " url-auth)
    (-> (response/redirect url-auth)
        ;(assoc :headers {"Content-Type" "text/html"})
        )))
