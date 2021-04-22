(ns webly.user.oauth2.events
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [webly.user.notifications.core :refer [add-notification]]
   [webly.user.oauth2.provider :refer [providers url-authorize]]
   [webly.user.oauth2.redirect :refer [register-callback]]))

;; LOGIN

(defn  current-url []
  (-> js/window .-location .-href))

(defn open-authorize-window [db provider]
  (let [url-auth (url-authorize (:config db) provider (current-url))]
    (info "opening oauth2 window: " url-auth)
    (.open js/window
           url-auth ; "/oauth2/github/auth"
           (str "Webly OAuth " (name provider))
           "width=500,height=600")))

(reg-event-db
 :oauth2/login
 (fn [db [_ provider]]
   (let [window (open-authorize-window db provider)]
     (assoc-in db [:oauth-window] window))))

(defn safe [token]
  (if-let [access-token (:access_token token)]
    (-> token
        (rename-keys {:access_token :access-token})
        ;(assoc :access-token access-token)
        ;(dissoc :acess_token)
        )
    token))

(reg-event-db
 :oauth2/save-token
 (fn [db [_ provider token]]
   (let [token (safe token)
         access-token (:access-token token)
         oauth-window (:oauth-window db)]
     (info "saving token " provider token)
     (if access-token
       (do
         (dispatch [:oauth2/get-user provider])
         (when oauth-window
           (.close oauth-window))
         (-> db
             (assoc-in [:token provider] token)
             (dissoc :oauth-window)))
       (assoc-in db [:token provider] token)))))

;; redirect

#_(defn message-event-handler
    [e]
    (info "message received: " e)
    (dispatch [:remote-oauth (.. e -data -code) (.. e -data -state)]))

;(js/window.addEventListener "message" message-event-handler)

(defn- oauth-redirect-dispatch [data]
  (dispatch [:oauth2/redirect data]))

(register-callback oauth-redirect-dispatch)

(reg-event-db
 :oauth2/redirect
 (fn [db [_ data]]
   (let [p (:provider data)
         provider (get providers p)
         parse (:parse provider)
         parse-dispatch (:parse-dispatch provider)
         token (parse data)]
     (when parse-dispatch
       (dispatch parse-dispatch))
     (if token
       (dispatch [:oauth2/save-token p token])
       (add-notification :danger "oauth login error (token not received)"))
     (assoc-in db [:token p] token))))

;; LOGOUT

(reg-event-fx
 :oauth2/logout
 (fn [{:keys [db]} [_ service]]
   (let [new-db (update-in db [:token] dissoc service)]
     {:db       new-db})))
