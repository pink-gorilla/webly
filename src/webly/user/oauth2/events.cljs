(ns webly.user.oauth2.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [cljs.reader :refer [read-string]]
   [cemerick.url :as url]
   [reagent.core :as r]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   ["oauth2-popup-flow" :refer [OAuth2PopupFlow]]
   [webly.user.oauth2.provider :refer [providers]]))

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
 :oauth2/login
 (fn [{db :db} [_ provider]]
   (let [cprovider (get providers provider)
         _ (println "oauth provider: " provider)
         config (get-in db [:config :oauth2 provider])
         redirect (redirect-config provider)
         config (merge cprovider config redirect)
         _ (println "oauth provider: " provider " config: " config)
         auth (auth-create (clj->js config))]
     (if (.loggedIn auth)
       (println "already logged in")
       (do (println "logging in..")
           (.tryLoginPopup auth))))
   nil))

(reg-event-db
 :oauth2/save-token
 (fn [db [_ provider token]]
   (println "saving token " provider token)
   (assoc-in db [:token provider] token)))

(reg-event-db
 :oauth2/redirect
 (fn [db [_ data]]
   ;(println "rdd" data)
   (let [p (:provider data)
         provider (get providers p)
         parse (:parse provider)
         parse-dispatch (:parse-dispatch provider)
         ;_ (println "parse provider: " p provider)
         token (parse data)]
     (when parse-dispatch
       (dispatch parse-dispatch))
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