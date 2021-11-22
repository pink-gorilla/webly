(ns webly.app.status.events
  (:require
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 :webly/set-status
 (fn [db [_ field value]]
   (assoc-in db [:webly/status field] value)))

(reg-event-db
 :webly/status
 (fn [db [_ state]]
   (assoc-in db [:webly/status :state] state)))

(reg-event-db
 :webly/status-css
 (fn [db [_ status]]
   (assoc-in db [:webly/status :css] status)))