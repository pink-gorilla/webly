(ns modular.oauth2.provider.google)

#_(defn parse-authorize-token-response [{:keys [anchor]}]
    ; #access_token=ya29.a0ARrdaM9mY4gaGPSU_5pMhS7x3wsgrPhDWhGy0fQVIwlsz7soPBlLVnAAEYQWl9SudGnfmapQ_2dq1oa6jS-SlJlR59cniSm1TAFkrK2KEqmBnvJHNI-mux6GDFtuVh-st5eysR97Z3xHSfjkxhsf9QknOZLv
    ;  &token_type=Bearer
    ;  &expires_in=3599
    ;  &scope=email%20https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/drive.metadata%20https://www.googleapis.com/auth/docs%20https://www.googleapis.com/auth/drive%20https://www.googleapis.com/auth/drive.appdata%20openid%20https://www.googleapis.com/auth/drive.file%20https://www.googleapis.com/auth/cloud-platform%20https://www.googleapis.com/auth/drive.metadata.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly%20https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/gmail.readonly%20https://www.googleapis.com/auth/drive.photos.readonly&authuser=0&prompt=none
    (let [{:keys [access_token scope expires_in token_type]} anchor]
      {:access-token access_token
       :scope scope
       :expires #?(:cljs (js/parseInt expires_in)
                   :clj (:expires_in anchor))
       :type token_type}))

(defn parse-authorize-code-response [{:keys [query]}]
  ;# :query {:scope "email+https://www.googleapis.com/auth/drive.metadata.readonly+https://www.googleapis.com/auth/drive.appdata+openid+https://www.googleapis.com/auth/drive.file+https://www.googleapis.com/auth/drive.metadata+https://www.googleapis.com/auth/drive+https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/drive.readonly+https://www.googleapis.com/auth/gmail.readonly+https://www.googleapis.com/auth/drive.photos.readonly+https://www.googleapis.com/auth/spreadsheets+https://www.googleapis.com/auth/cloud_search+https://www.googleapis.com/auth/spreadsheets.readonly+https://www.googleapis.com/auth/calendar+https://www.googleapis.com/auth/cloud-platform+https://www.googleapis.com/auth/docs", 
;            :prompt "none", 
;            :authuser "0", 
;            :code "4/0AX4XfWh8wjf3vIem2f-1VTW5zMdppmmY3DSex9vVfsSANTmf2kDqtyBs049qTlXc0f54XQ"}, 
; :provider :google}
  (let [{:keys [scope code prompt authuser]} query]
    {:scope scope
     :code code}))

(defn user-parse [data]
  {:user (:id data)
   :email (:email data)})

(defn api-request-auth-header [token]
  {"Authorization" (str "Bearer " token)})

; https://oauth2.example.com/code?state=security_token%3D138r5719ru3e1%26url%3Dhttps%3A%2F%2Foa2cb.example.com%2FmyHome&code=4/
; https://developers.google.com/identity/protocols/oauth2/openid-connect#createxsrftoken
; https://developers.google.com/identity/protocols/oauth2#5.-refresh-the-access-token,-if-necessary.
; https://developers.google.com/accounts/docs/OAuth2WebServer

(def config
  {; authorize
   :authorize-uri "https://accounts.google.com/o/oauth2/v2/auth"
   :authorize-response-type "code" ; "token" ; 
   :parse-authorize-response parse-authorize-code-response

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

; Google refresh token -> access token. 
;refresh_token: <REFRESH_TOKEN_FOR_THE_USER>
;grant_type: refresh_token

;In order to get an access token with a refresh token, you just need to ask for the offline access
; type (for example in PHP: $client->setAccessType("offline");) and you will get it.
; Just keep in mind you will get the access token with the refresh token only in 
; the first authorization, so make sure to save that access token in the first time
; , and you will be able to use it anytime.

; Token query 
; Grant type,: authorization_code
; Code 

; refresh_token
; (Optional)
; Dieses Feld ist nur vorhanden, wenn der Parameter 
 ; access_type in der Authentifizierungsanforderung 
;   auf offline wurde. Weitere Informationen finden Sie unter Aktualisieren von Token .
