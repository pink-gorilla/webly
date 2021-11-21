(ns modular.oauth2.provider.google)

;{"token_type" "Bearer", 
;"access_token" "ya29.a0AfH6SMD4K4sFxQ8LaWVOv_gPteXEdmGjRIFlWXxtJ7z0trs5p6bpoNmQ3ebN", 
;"scope" "https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/spreadsheets.readonly",
; "expires_in" "3599"}

;#access_token=ya29.a0ARrdaM9mY4gaGPSU_5pMhS7x3wsgrPhDWhGy0fQVIwlsz7soPBlLVnAAEYQWl9SudGnfmapQ_2dq1oa6jS-SlJlR59cniSm1TAFkrK2KEqmBnvJHNI-mux6GDFtuVh-st5eysR97Z3xHSfjkxhsf9QknOZLv
;&token_type=Bearer
;&expires_in=3599
;&scope=email%20https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/drive.metadata%20https://www.googleapis.com/auth/docs%20https://www.googleapis.com/auth/drive%20https://www.googleapis.com/auth/drive.appdata%20openid%20https://www.googleapis.com/auth/drive.file%20https://www.googleapis.com/auth/cloud-platform%20https://www.googleapis.com/auth/drive.metadata.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly%20https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/gmail.readonly%20https://www.googleapis.com/auth/drive.photos.readonly&authuser=0&prompt=none

(defn parse-authorize-response [{:keys [anchor]}]
  (let [{:keys [access_token scope expires_in token_type]} anchor]
    {:access-token access_token
     :scope scope
     :expires #?(:cljs (js/parseInt expires_in)
                 :clj (:expires_in anchor))
     :type token_type}))

(defn user-parse [data]
  {:user (:id data)
   :email (:email data)})

(defn api-request-auth-header [token]
  {"Authorization" (str "Bearer " token)})

(def config
  {; authorize
   :authorize-uri "https://accounts.google.com/o/oauth2/v2/auth"
   :authorize-response-type "token"
   :parse-authorize-response parse-authorize-response

    ; access token
   :token-uri "https://www.googleapis.com/oauth2/v4/token"
   ;"https://accounts.google.com/o/oauth2/v2/access_token"
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header api-request-auth-header
   :endpoints {:userinfo "https://www.googleapis.com/oauth2/v2/userinfo"
               :search "https://api.goog.io/v1/search"
               :drive-files-list "https://www.googleapis.com/drive/v3/files"
               ;https://developers.google.com/drive/api/v3/reference/files/list
               }
; userinfo
   :user "https://www.googleapis.com/oauth2/v2/userinfo"
   :user-parse user-parse})

; https://developers.google.com/identity/protocols/oauth2#5.-refresh-the-access-token,-if-necessary.

; Google refresh token -> access token. 
;refresh_token: <REFRESH_TOKEN_FOR_THE_USER>
;grant_type: refresh_token

; https://developers.google.com/accounts/docs/OAuth2WebServer

;In order to get an access token with a refresh token, you just need to ask for the offline access
; type (for example in PHP: $client->setAccessType("offline");) and you will get it. Just keep in mind you will get the access token with the refresh token only in the first authorization, so make sure to save that access token in the first time, and you will be able to use it anytime.

; return the 401 only for unauthenticated requests and use an HTTP 403 Forbidden instead for requests that to not have adequate authorization,

