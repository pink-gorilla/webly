(ns webly.oauth2.events
  (:require
   [reagent.core :as r]
   [taoensso.timbre :refer-macros [info error]]
   [cljs.reader :refer [read-string]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [cemerick.url :as url]
   ["oauth2-popup-flow" :refer [OAuth2PopupFlow]]))


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
  {:access-token (get query "code")})



(def providers
  {:test {:authorizationUri "https://example.com/oauth/authorize"
          :clientId "YOUR_CLIENT_ID"
          ;:redirectUri "http://localhost:8000/redirect/test"
          :scope "openid profile"}
   :github {:authorizationUri "https://github.com/login/oauth/authorize"
            :accessTokenUri "https://github.com/login/oauth/access_token"
            :accessTokenResponseKey "id_token"
            :scope           "user:email gist"
            ;:redirectUri "http://localhost:8000/oauth2/redirect/github"
            :parse parse-github}
   :google {:authorizationUri "https://accounts.google.com/o/oauth2/v2/auth"
            :accessTokenUri "https://accounts.google.com/o/oauth2/v2/access_token"
            :accessTokenResponseKey "id_token"
            :scope           "https://www.googleapis.com/auth/spreadsheets.readonly https://www.googleapis.com/auth/drive.readonly"
            ;:redirectUri "http://localhost:8000/oauth2/redirect/google"
            :parse parse-google}})

(defn clear-error [state]
  (assoc-in state [:user-auth :error] nil))

(defn message-event-handler
  "Message handler for oauth login (from ::open-oauth-window).
  This is a named function to prevent the handler from being added
  multiple times."
  [e]
  (info "message received: " e)
  (dispatch [:remote-oauth (.. e -data -code) (.. e -data -state)]))

#_(reg-event-fx
   :oauth2/open-window
   (fn [{db :db} [_ provider]]
     (js/window.addEventListener "message" message-event-handler)
     (case provider
       :github (do (info "github oauth2 ..")
                   (.open js/window
                          "/oauth2/github/auth"
                          "GitHub OAuth"
                          "width=500,height=600")
                   (info "window should have been opened.."))
       (error "unknown oauth2 provider: " provider))
     {:db (-> db
              clear-error
              (assoc-in [:user-auth :oauth-provider] provider))}))




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

(def current (r/atom nil))


(defn auth-create [c]
  (println "creds: " c)
  (let [a (OAuth2PopupFlow. c)]
    (reset! current a)
    a))


(defn redirect-config [provider]
  (let [base (url/url (-> js/window .-location .-href))
        redirect (assoc base :path (str "/oauth2/redirect/" (name provider)))
        redirect-url (.toString redirect)]
    (println "redirect url: " redirect-url)
    {:redirectUri redirect-url}))


(reg-event-fx
 :oauth2/open-window
 (fn [{db :db} [_ provider]]
   (let [cprovider (get providers provider)
         _ (println "oauth provider: " provider)
         config (get-in db [:config :oauth2 provider])
         redirect (redirect-config provider)
         config (merge cprovider config redirect)
         _ (println "oauth provider: " provider " config: " config)
         auth (auth-create (clj->js config))]
     (if (.loggedIn auth)
       (println "already loggedin")
       (do (println "logging in..")
           (.tryLoginPopup auth))))
   nil))


(reg-event-db
 :oauth2/redirect
 (fn [db [_ data]]
   ;(println "rdd" data)
   (let [p (:provider data)
         provider (get providers p)
         parse (:parse provider)
         ;_ (println "parse provider: " p provider)
         token (parse data)]
     (if token
       (do (println "token " p "rcvd" token)
           (assoc-in db [:token p] token))
       (;(add-notification :danger "Error loading config")
        db)))))

; https://stackoverflow.com/questions/28230845/communication-between-tabs-or-windows
; https://developer.mozilla.org/en-US/docs/Web/API/Broadcast_Channel_API
(def bc (js/BroadcastChannel. "oauth2_redirect_channel"))

(set! (.. bc -onmessage)
      (fn [ev]
        ;(println "chan msg: " (js->clj ev))
        (let [data (read-string (. ev -data))]
          (println "chan data: " data)
          (dispatch [:oauth2/redirect data]))))