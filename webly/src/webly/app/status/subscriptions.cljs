(ns webly.app.status.subscriptions
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :webly/status-of
 (fn [db [_ field]]
   (get-in db [:webly/status field])))

(rf/reg-sub
 :webly/status
 (fn [db _]
   (get-in db [:webly/status])))

(rf/reg-sub
 :webly/status-show-app
 (fn [db _]
   (let [status (get-in db [:webly/status])
         {:keys [state css]} status]
     (and (= state :running)
          (= css :loaded)))))