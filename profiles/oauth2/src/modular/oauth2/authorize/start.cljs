(ns modular.oauth2.authorize.start
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [modular.oauth2.authorize.redirect :refer [register-callback]]
   [modular.oauth2.store] ; side effects
   [modular.oauth2.provider :refer [providers url-authorize]]))

;; LOGIN

(defn  current-url []
  (-> js/window .-location .-href))

(defn open-authorize-window [db provider]
  (let [url-auth (url-authorize (:config db) provider (current-url))]
    (info "opening oauth2 window: " url-auth)
    (.open js/window
           url-auth
           (str "Webly OAuth2 " (name provider))
           "width=500,height=600")))

(reg-event-db
 :oauth2/login
 (fn [db [_ provider]]
   (let [window (open-authorize-window db provider)]
     (assoc-in db [:oauth-window] window))))