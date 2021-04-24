(ns webly.user.status.events
  (:require
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 :webly/status
 (fn [db [_ state]]
   (assoc-in db [:webly/status :state] state)))

(reg-event-db
 :webly/status-css
 (fn [db [_ status]]
   (assoc-in db [:webly/status :css] status)))