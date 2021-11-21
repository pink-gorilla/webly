(ns modular.oauth2.authorize.token-handler
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [ring.util.response :as res]
   [ring.util.codec :as codec]
 ;   [clojure.data.codec.base64 :as b64]
   [modular.base64 :refer [base64-encode]]
   [ajax.core :as ajax]
   [modular.config :refer [get-in-config config-atom]]
   [modular.oauth2.provider :refer [full-provider-config]]))

; (codec/form-encode 

;(defn token-request [url params])

; xero:
;Content-Type: application/x-www-form-urlencoded

;The request body will need to contain the grant type (authorization_code), code and redirect_uri

(defn auth-header-basic [token]
  {"Authorization" (str "Basic " token)})

(defn auth-header-oauth-token [client-id client-secret]
  {"Authorization" (str "Basic " (base64-encode (str client-id ":" client-secret)))})

(defn token-handler [req]
  (debug "token handler: " req)
  (let [code (get-in req [:query-params "code"])
        provider (-> (get-in req [:query-params "provider"])
                     keyword)
        url-redirect (get-in req [:query-params "url-redirect"])
        p (promise)
        provider-config (full-provider-config @config-atom provider)
        {:keys [token-uri client-id client-secret]} provider-config]
    (info "getting token for provider " provider " code :" code "client-id:" client-id " redirect-url: " url-redirect)
    (ajax/POST token-uri ; "https://github.com/login/oauth/access_token"
      :headers (auth-header-oauth-token client-id client-secret)
      :params {:client_id	 client-id
               :client_secret client-secret
               :code code
               :grant_type "authorization_code" ; xero
               :redirect_uri url-redirect ; The same redirect URI that was used when requesting the code
               }
      ; :format (ajax/json-request-format) ; {:keywords? true}
      :format (ajax/url-request-format) ; xero
      :timeout         5000                     ;; optional see API docs
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [res]
                 (info provider "/get-token success: " res)
                 (deliver p res))
      :error-handler (fn [res]
                       (error provider "/get-token error: " res)
                       (deliver p res)
                       ;(reject p res)
                       ))
    (res/response @p)))

(comment
  (handler-github-redirect {})

  ;
  )
