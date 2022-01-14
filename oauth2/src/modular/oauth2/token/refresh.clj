

(ns modular.oauth2.token.refresh
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
 ;   [clojure.data.codec.base64 :as b64]
   [modular.base64 :refer [base64-encode]]
   [ajax.core :as ajax]
   [modular.config :refer [get-in-config config-atom]]
   [modular.oauth2.provider :refer [full-provider-config]]
   [modular.oauth2.token.store :refer [load-token save-token]]
   [modular.oauth2.token.sanitize :refer [sanitize-token]]))

(defn auth-header-basic [token]
  {"Authorization" (str "Basic " token)})

(defn auth-header-oauth-token [client-id client-secret]
  ; Authorization: "Basic " + base64encode(client_id + ":" + client_secret)
  {"Authorization" (str "Basic " (base64-encode (str client-id ":" client-secret)))})

(defn refresh-auth-token [provider]
  (info "refreshing access token for: " provider)
  (let [token (load-token provider)
        refresh-token (:refresh-token token)
        p (promise)
        provider-config (full-provider-config @config-atom provider)
        {:keys [token-uri client-id client-secret]} provider-config
        header (auth-header-oauth-token client-id client-secret)
        params {:client_id	 client-id
                :client_secret client-secret
                :refresh_token refresh-token
                :grant_type "refresh_token"}]
    (info "refreshing token for provider " provider
          "client-id:" client-id "refresh token: " refresh-token
          "header: " header
          "params: " params)
    (ajax/POST token-uri
      :headers header
      ; grant_type=refresh_token
        ;refresh_token=Your refresh token
      :params params
      ; :format (ajax/json-request-format) ; {:keywords? true}
      :format (ajax/url-request-format) ; xero
      :timeout         5000                     ;; optional see API docs
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [res]
                 (info "refresh-token " provider "success!")
                 (debug provider "/refresh-token success: " res)
                 (let [token-new (sanitize-token res)
                       token-new (merge token token-new)
                       ; some providers include a new refresh token, some dont
                       ; so we merge the tokens so we use all fields from the new token,
                       ; and carry forward the old token (in case we do not get a new refresh-token)
                       ;(assoc token-new :refresh-token refresh-token)
                       ]
                   (debug "new access-token: " (:access-token token-new))
                   (save-token provider token-new)
                   (deliver p res)))
      :error-handler (fn [res]
                       (error provider "/refresh-token error: " res)
                       (deliver p res)
                       ;(reject p res)
                       ))
    @p))

(comment
  (handler-github-redirect {})

  ;
  )
