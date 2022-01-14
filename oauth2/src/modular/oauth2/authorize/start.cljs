(ns modular.oauth2.authorize.start
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [modular.oauth2.authorize.redirect :refer [register-callback]]
   [modular.oauth2.token.save-handler] ; side effects
   [modular.oauth2.provider :refer [url-authorize]]
   [modular.oauth2.token.sanitize :refer [sanitize-token]]))

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

(rf/reg-event-db
 :oauth2/authorize-start
 (fn [db [_ provider oauth-success-event]]
   (let [window (open-authorize-window db provider)]
     (assoc-in db [:oauth-authorize] {:window window
                                      :success-event oauth-success-event}))))

(rf/reg-event-db
 :oauth2/authorize-success
 (fn [db [_ provider token]]
   (let [token (sanitize-token token)
         access-token (:access-token token)
         {:keys [window success-event]} (:oauth-authorize db)]
     (info "oauth2 authorize success: " provider token)
     (if access-token
       (do
         (rf/dispatch [:oauth2/get-user provider])
         (rf/dispatch [success-event provider token])
         (when window
           (.close window))
         (-> db
             (assoc-in [:token provider] token)
             (dissoc :oauth-authorize)))
       (assoc-in db [:token provider] token)))))