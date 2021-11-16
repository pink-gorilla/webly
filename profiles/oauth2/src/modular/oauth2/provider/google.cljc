(ns modular.oauth2.provider.google)

;{"token_type" "Bearer", 
;"access_token" "ya29.a0AfH6SMD4K4sFxQ8LaWVOv_gPteXEdmGjRIFlWXxtJ7z0trs5p6bpoNmQ3ebN", 
;"scope" "https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/spreadsheets.readonly",
; "expires_in" "3599"}

(defn parse-google [{:keys [anchor]}]
  {:access-token (:access_token anchor)
   :scope (:scope anchor)
   :expires #?(:cljs (js/parseInt (:expires_in anchor))
               :clj (:expires_in anchor))})

(defn user-parse [data]
  {:user (:id data)
   :email (:email data)})

(defn auth-header [token]
  {"Authorization" (str "Bearer " token)})

(def config
  {:authorize-uri "https://accounts.google.com/o/oauth2/v2/auth"
   :access-token-uri "https://accounts.google.com/o/oauth2/v2/access_token"
   :response-type "token"
   :accessTokenResponseKey "id_token"
   :parse parse-google
   :user "https://www.googleapis.com/oauth2/v2/userinfo"
   :user-parse user-parse
   :auth-header auth-header})