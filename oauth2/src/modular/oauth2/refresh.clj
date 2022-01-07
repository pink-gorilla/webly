

(ns modular.oauth2.refresh
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
 ;   [clojure.data.codec.base64 :as b64]
   [modular.base64 :refer [base64-encode]]
   [ajax.core :as ajax]
   [modular.config :refer [get-in-config config-atom]]
   [modular.oauth2.provider :refer [full-provider-config]]
   [modular.oauth2.store :refer [load-token save-token]]))

(defn auth-header-basic [token]
  {"Authorization" (str "Basic " token)})

(defn auth-header-oauth-token [client-id client-secret]
  {"Authorization" (str "Basic " (base64-encode (str client-id ":" client-secret)))})

(defn refresh-auth-token [provider]
  (info "refreshing access token for: " provider)
  (let [token (load-token provider)
        refresh_token (:refresh_token token)
        p (promise)
        provider-config (full-provider-config @config-atom provider)
        {:keys [token-uri client-id client-secret]} provider-config]
    (info "refreshing token for provider " provider "client-id:" client-id "refresh token: " refresh_token)
    (ajax/POST token-uri
      :headers (auth-header-oauth-token client-id client-secret)
      :params {:client_id	 client-id
               :client_secret client-secret
               :refresh_token refresh_token
               :grant_type "refresh_token"}
      ; :format (ajax/json-request-format) ; {:keywords? true}
      :format (ajax/url-request-format) ; xero
      :timeout         5000                     ;; optional see API docs
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [res]
                 (info provider "/refresh-token success: " res)
                 (let [id-token (:id_token res)
                       access-token (:access_token res)
                       token-new (assoc token :id-token id-token
                                        :access-token access-token)]
                   (info "new access-token: " access-token)
                   (info "new id-token: " id-token)
                   (save-token provider token-new))
                 (deliver p res))
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
