(ns  modular.oauth2.provider.woo
  (:require
   [modular.oauth2.protocol :refer [provider-config]]))

; https://woocommerce.github.io/woocommerce-rest-api-docs/#rest-api-keys

#_(defn parse-authorize-token-response [{:keys [anchor]}]
    ; #access_token=ya29.a0ARrdaM9mY4gaGPSU_5pMhS7x3wsgrPhDWhGy0fQVIwlsz7soPBlLVnAAEYQWl9SudGnfmapQ_2dq1oa6jS-SlJlR59cniSm1TAFkrK2KEqmBnvJHNI-mux6GDFtuVh-st5eysR97Z3xHSfjkxhsf9QknOZLv
    ;  &token_type=Bearer
    ;  &expires_in=3599
    ;  &scope=email%20https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/drive.metadata%20https://www.googleapis.com/auth/docs%20https://www.googleapis.com/auth/drive%20https://www.googleapis.com/auth/drive.appdata%20openid%20https://www.googleapis.com/auth/drive.file%20https://www.googleapis.com/auth/cloud-platform%20https://www.googleapis.com/auth/drive.metadata.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly%20https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/gmail.readonly%20https://www.googleapis.com/auth/drive.photos.readonly&authuser=0&prompt=none
    (let [{:keys [access_token scope expires_in token_type]} anchor]
      {:access-token access_token
       :scope scope
       ;:expires #?(:cljs (js/parseInt expires_in)
       ;            :clj (:expires_in anchor))
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

(def config
  {; authorize
   :authorize-uri "https://www.crbclean.com/wc-auth/v1/authorize"
  ; :authorize-query-params {
  ;   }
   :authorize-redirect-uri-name :return_url ; URL the user will be redirected to after authentication
   :parse-authorize-response parse-authorize-code-response

    ; access token
   :token-uri "https://www.googleapis.com/oauth2/v4/token"
   ;"https://accounts.google.com/o/oauth2/v2/access_token"
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header api-request-auth-header
   :endpoints {:userinfo nil
               :orders "http://www.crbclean.com/wp-json/wc/v3/orders"}
   ; userinfo
   :user nil
   :user-parse user-parse

   :icon "fab fa-google-plus"})

(defmethod provider-config :woo [_]
  config)

; https://woocommerce.github.io/woocommerce-rest-api-docs/
