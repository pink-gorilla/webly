(ns modular.oauth2.authorize.redirect-events
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [modular.oauth2.authorize.redirect :refer [register-callback]]
   [modular.oauth2.token.save-handler] ; side effects
   [modular.oauth2.protocol :refer [provider-config]]
   [modular.oauth2.provider :refer [url-authorize]]))

;; redirect

(defn- oauth-redirect-dispatch [data]
  (rf/dispatch [:oauth2/redirect data]))

(register-callback oauth-redirect-dispatch)

(rf/reg-event-fx
 :oauth2/redirect
 (fn [{:keys [db]} [_ data]]
   (let [p (:provider data)
         provider (provider-config p)
         parse-authorize-response (:parse-authorize-response provider)
         auth-result (parse-authorize-response data)]
     (if (:code auth-result)
       (rf/dispatch [:oauth2/code->token p auth-result])
       (rf/dispatch [:oauth2/authorize-success p auth-result]))

     nil)))


