(ns modular.oauth2.authorize.userinfo
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [clojure.string :as str]
   [ajax.core :as ajax]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [modular.oauth2.provider :refer [get-provider-config parse-userinfo]]))

(reg-event-db
 :oauth2/get-user
 (fn [{:keys [db]} [_ provider]]
   (info "oauth/after-login:" provider)
   (let [config (get-provider-config provider)
         uri (:user config)]
     (dispatch [:request provider uri :oauth2/set-user]))
   db))

(reg-event-db
 :oauth2/set-user
 (fn [db [_ provider data]]
   (let [{:keys [user email]}
         (parse-userinfo provider data)]
     (info "oauth2/set-user:" provider user email)
     (dispatch [:oauth2/logged-in provider])
     (-> db
         (assoc-in  [:token provider :user] user)
         (assoc-in  [:token provider :email] email)))))