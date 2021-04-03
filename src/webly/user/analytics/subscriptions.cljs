(ns webly.user.analytics.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :analytics/config
 (fn [db _]
   (get-in db [:config :google-analytics])))
