(ns webly.user.settings.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :settings
 (fn [db _]
   (get-in db [:settings])))