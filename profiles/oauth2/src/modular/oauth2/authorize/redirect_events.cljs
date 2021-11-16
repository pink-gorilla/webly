(ns modular.oauth2.authorize.redirect-events
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [modular.oauth2.authorize.redirect :refer [register-callback]]
   [modular.oauth2.store] ; side effects
   [modular.oauth2.provider :refer [providers url-authorize]]
   [webly.user.notifications.core :refer [add-notification]]))

;; redirect

#_(defn message-event-handler
    [e]
    (info "message received: " e)
    (dispatch [:remote-oauth (.. e -data -code) (.. e -data -state)]))

;(js/window.addEventListener "message" message-event-handler)

(defn- oauth-redirect-dispatch [data]
  (rf/dispatch [:oauth2/redirect data]))

(register-callback oauth-redirect-dispatch)

(rf/reg-event-db
 :oauth2/redirect
 (fn [db [_ data]]
   (let [p (:provider data)
         provider (get providers p)
         parse (:parse provider)
         parse-dispatch (:parse-dispatch provider)
         token (parse data)]
     (when parse-dispatch
       (rf/dispatch parse-dispatch))
     (if token
       (do (rf/dispatch [:oauth2/save-token p token])
           (assoc-in db [:token p] token))
       (do (rf/dispatch [:oauth2/login-error p])
           db)))))

;; LOGOUT

(rf/reg-event-fx
 :oauth2/logout
 (fn [{:keys [db]} [_ service]]
   (let [new-db (update-in db [:token] dissoc service)]
     {:db       new-db})))
