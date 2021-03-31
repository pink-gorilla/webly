(ns webly.oauth2.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx dispatch]]
   ["oauth2-popup-flow" :refer [OAuth2PopupFlow]]))

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


(def iex  #js {:exp "number"
               :other "string"
               :stuff "string"
               :username "string"})

(def creds #js {:authorizationUri "https://example.com/oauth/authorize"
                :clientId "YOUR_CLIENT_ID"
                :redirectUri "http://localhost:8080/redirect"
                :scope "openid profile"})


(def creds-github #js {:authorizationUri "https://github.com/login/oauth/authorize"
                       :accessTokenUri "https://github.com/login/oauth/access_token"
                        :accessTokenResponseKey "id_token"
                       :clientId        "25928b07df85fb46bf15"
                       :clientSecret    "ab0502c40f986c09b8de3d450505ee4b517bb4cf"
                       :scope           "user:email gist"
                       :redirectUri "http://localhost:8080/oauth2/github/landing"})



#_{:authorizationUri "https://formandfocus.auth0.com/authorize"
   :clientId "v90UOqUtmib6bTNIm3zHuYboekqoAXwN"
   :redirectUri "http://localhost:8080/redirect"
   :scope "openid profile"
   :responseType "id_token"
   :accessTokenResponseKey "id_token"
   :additionalAuthorizationParameters {:nonce (.toString (js/Math .random))}}


(defn auth-create [c]
   (println "creds: " c)
   (OAuth2PopupFlow. c)
  )


(reg-event-fx
 :oauth2/open-window
 (fn [{db :db} [_ provider]]
  
   (let [;TokenPayload (js/interface iex)
         ; <TokenPayload>
         auth (auth-create creds-github)]
     (if (.loggedIn auth)
       (println "github already loggedin")
       (do (println "logging in to github")
           (.tryLoginPopup auth))))

   nil))