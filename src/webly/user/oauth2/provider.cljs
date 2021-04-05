(ns webly.user.oauth2.provider
  (:require
   [reagent.core :as r]
   [taoensso.timbre :refer-macros [info error]]
   [cljs.reader :refer [read-string]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [cemerick.url :as url]
   [ajax.core :as ajax]))

;{"token_type" "Bearer", 
;"access_token" "ya29.a0AfH6SMD4K4sFxQ8LaWVOv_gPteXEdmGjR_pYKnBVLvmbBbs4284KKQo180D8SMJbqRcgg46-F73i8NIZzBCzrpwYWeuNQgt5-8e1SIFpzDWuTfuVkCxP-8GEfzrho2nZYu1lWXxtJ7z0trs5p6bpoNmQ3ebN", 
;"scope" "https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/spreadsheets.readonly",
; "expires_in" "3599"}
(defn parse-google [{:keys [anchor]}]
  {:access-token (get anchor "access_token")
   :scope (get anchor "scope")
   :expires (get anchor "expires_in")})

; :query {code 27aefeb351c395d63c34}}
(defn parse-github [{:keys [query]}]
  {:code (get query "code")})

(def providers
  {:test {:authorizationUri "https://example.com/oauth/authorize"
          :clientId "YOUR_CLIENT_ID"
          ;:redirectUri "http://localhost:8000/redirect/test"
          :scope "openid profile"}
   :github {:authorizationUri "https://github.com/login/oauth/authorize"
            :accessTokenUri "https://github.com/login/oauth/access_token"
            :accessTokenResponseKey "id_token"
            :scope           "user:email gist repo"
            ;:redirectUri "http://localhost:8000/oauth2/redirect/github"
            :parse parse-github
            :parse-dispatch [:github/code->token]}
   :google {:authorizationUri "https://accounts.google.com/o/oauth2/v2/auth"
            :accessTokenUri "https://accounts.google.com/o/oauth2/v2/access_token"
            :accessTokenResponseKey "id_token"
            :scope           "https://www.googleapis.com/auth/spreadsheets.readonly https://www.googleapis.com/auth/drive.readonly"
            ;:redirectUri "http://localhost:8000/oauth2/redirect/google"
            :parse parse-google}})

(reg-event-fx
 :github/code->token
 (fn [{:keys [db]} _]
   (let [code (get-in db [:token :github :code])
         {:keys [clientId clientSecret]} (get-in db [:config :oauth2 :github])]
     (info "github code -> token .. code: " code " clientId " clientId)
     {:db       db
      :http-xhrio {:method          :get
                   :uri            "/api/oauth2/github/token" ;  "https://github.com/login/oauth/access_token"
                   :params {;:client_id	 clientId
                            ;:client_secret clientSecret
                            :code code}
                   ;:format (ajax/json-request-format {:keywords? true})
                   :timeout         5000                     ;; optional see API docs
                   :response-format (ajax/json-response-format {:keywords? true});; IMPORTANT!: You must provide this.
                   :on-success      [:oauth2/save-token :github]
                   :on-failure      [:oauth2/save-error]}})))





; https://github.com/login/oauth/authorize?
; client_id=
; &response_type=token
; &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fgithub%2Flanding
; &scope=user%3Aemail%20gist


;  scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly&
 ;include_granted_scopes=true&
; response_type=token&
; state=state_parameter_passthrough_value&
; redirect_uri=https%3A//oauth2.example.com/code&
; client_id=client_id

; http://localhost:8000/oauth2/google/token
; #access_token=ya29.a0AfH6SMBizCNXcAu2WM_P4quZ2Z5z3rHhz0824AO-c_nO2AOiDW7NT3kT3bDNw8wK5i6xMa8ysgKFlwTQv5vvpVCkepmvCGSvm6iwkvVsseaaSOB7Af4uJzX5wbgrZ_4F_6Dkrp9rMO48RtI9Gp2gzvEOqxdT
; &token_type=Bearer
; &expires_in=3599
; &scope=https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly


#_{:authorizationUri "https://formandfocus.auth0.com/authorize"
   :clientId "v90UOqUtmib6bTNIm3zHuYboekqoAXwN"
   :redirectUri "http://localhost:8080/redirect"
   :scope "openid profile"
   :responseType "id_token"
   :accessTokenResponseKey "id_token"
   :additionalAuthorizationParameters {:nonce (.toString (js/Math .random))}}