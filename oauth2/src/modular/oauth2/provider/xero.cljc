(ns modular.oauth2.provider.xero
  (:require
   [modular.oauth2.protocol :refer [provider-config]]))

(defn parse-authorize-response [{:keys [query]}] ;anchor
  {:scope (:scope query)
   :code (:code query)})

;; api requests

(defn api-request-auth-header [token]
  {"Authorization" (str "Bearer " token)})

(defn user-parse [data]
  {:user (get-in data [:Organisations :Name])
   :email "no email"})

(def config
  {; authorize
   :authorize-uri "https://login.xero.com/identity/connect/authorize"
   :authorize-response-type "code"
   :parse-authorize-response parse-authorize-response
   ; token  
   :token-uri "https://identity.xero.com/connect/token"
   :accessTokenResponseKey "id_token"
   ; api requests
   :auth-header api-request-auth-header
   :endpoints {; https://api-explorer.xero.com/
               :userinfo "https://api.xero.com/api.xro/2.0/Organisation"
               :contacts "https://api.xero.com/api.xro/2.0/Contacts"
               :branding-themes "https://api.xero.com/api.xro/2.0/BrandingThemes"}
   ; userinfo
   :user "https://api.xero.com/api.xro/2.0/Organisation"
   :user-parse user-parse})

(defmethod provider-config :xero [_]
  config)

;; Xero example for authroize request

; https://login.xero.com/identity/connect/authorize
; ?response_type=code
; &client_id=YOURCLIENTID
; &redirect_uri=YOURREDIRECTURI
; &scope=openid profile email accounting.transactions
; &state=123

