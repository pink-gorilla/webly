(ns modular.oauth2.authorize.redirect-events
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
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
