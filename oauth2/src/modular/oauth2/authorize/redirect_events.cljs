(ns modular.oauth2.authorize.redirect-events
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [modular.oauth2.authorize.redirect :refer [register-callback]]
   [modular.oauth2.store] ; side effects
   [modular.oauth2.provider :refer [providers url-authorize]]))

;; redirect

(defn- oauth-redirect-dispatch [data]
  (rf/dispatch [:oauth2/redirect data]))

(register-callback oauth-redirect-dispatch)

(rf/reg-event-fx
 :oauth2/redirect
 (fn [{:keys [db]} [_ data]]
   (let [p (:provider data)
         provider (get providers p)
         parse-authorize-response (:parse-authorize-response provider)
         auth-result (parse-authorize-response data)]
     (if (:code auth-result)
       (rf/dispatch [:oauth2/code->token p auth-result])
       (rf/dispatch [:oauth2/save-token p auth-result]))

;(when parse-dispatch
     ;  (rf/dispatch parse-dispatch))
     ;(if token
     ;  (do (rf/dispatch [:oauth2/save-token p token])
     ;      (assoc-in db [:token p] token))
     ;  (do (rf/dispatch [:oauth2/login-error p])
     ;      db))
     nil)))

;; LOGOUT

(rf/reg-event-fx
 :oauth2/logout
 (fn [{:keys [db]} [_ service]]
   (let [new-db (update-in db [:token] dissoc service)]
     {:db       new-db})))
