(ns modular.oauth2.provider.xero)

(defn parse-redirect [{:keys [query]}] ;anchor
  {:scope (:scope query)
   :code (:code query)})

;; api requests

(defn auth-header [token]
  {"Authorization" (str "Bearer " token)})

(defn user-parse [data]
  {:user (get-in data [:Organisations :Name])
   :email "no email"})

(def config
  {; authorize
   :authorize-uri "https://login.xero.com/identity/connect/authorize"
   :response-type "code"
   :parse-redirect parse-redirect

   ; access token  
   :access-token-uri "https://identity.xero.com/connect/token"
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header auth-header
   :endpoints {; https://api-explorer.xero.com/
               :userinfo "https://api.xero.com/api.xro/2.0/Organisation"
               :contacts "https://api.xero.com/api.xro/2.0/Contacts"
               :branding-themes "https://api.xero.com/api.xro/2.0/BrandingThemes"}
   ; userinfo
   :user "https://api.xero.com/api.xro/2.0/Organisation"
   :user-parse user-parse})

;; Xero example for authroize request

; https://login.xero.com/identity/connect/authorize
; ?response_type=code
; &client_id=YOURCLIENTID
; &redirect_uri=YOURREDIRECTURI
; &scope=openid profile email accounting.transactions
; &state=123

