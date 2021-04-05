(ns webly.user.oauth2.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :oauth2/tokens
 (fn [db [_]]
   (get-in db [:token])))

(reg-sub
 :oauth2/logged-in?
 (fn [db [_ service]]
   (some? (get-in db [:token service]))))