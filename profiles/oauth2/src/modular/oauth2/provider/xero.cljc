(ns modular.oauth2.provider.xero)

;{"token_type" "Bearer", 
;"access_token" "ya29.a0AfH6SMD4K4sFxQ8LaWVOv_gPteXEdmGjRIFlWXxtJ7z0trs5p6bpoNmQ3ebN", 
;"scope" "https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/spreadsheets.readonly",
; "expires_in" "3599"}

(defn parse-google [{:keys [anchor]}]
  {:access-token (:access_token anchor)
   :scope (:scope anchor)
   :expires #?(:cljs (js/parseInt (:expires_in anchor))
               :clj (:expires_in anchor))})

;; api requests

(defn auth-header [token]
  {"Authorization" (str "Bearer " token)})

(defn user-parse [data]
  {:user (get-in data [:Organisations :Name])
   :email "no email"})

(def config
  {; authorize
   :authorize-uri "https://login.xero.com/identity/connect/authorize"
   :response-type "token"
   ; access token
   :access-token-uri "https://identity.xero.com/connect/token"
   :accessTokenResponseKey "id_token"
   :parse parse-google
   ; api requests
   :auth-header auth-header
   :endpoints {; https://api-explorer.xero.com/
               :userinfo "https://api.xero.com/api.xro/2.0/Organisation"
               :contacts "https://api.xero.com/api.xro/2.0/Contacts"}
   ; userinfo
   :user "https://api.xero.com/api.xro/2.0/Organisation"
   :user-parse user-parse})

; https://login.xero.com/identity/connect/authorize
; ?response_type=code
; &client_id=YOURCLIENTID
; &redirect_uri=YOURREDIRECTURI
; &scope=openid profile email
; &state=123

; grant_type=authorization_code