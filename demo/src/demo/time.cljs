(ns demo.time
  (:require
   [re-frame.core :refer [reg-event-db reg-sub]]))

(reg-event-db
 :demo/time
 (fn [db [_ t]]
   (assoc db :time t)))

(reg-sub
 :demo/time
 (fn [db _]
   (:time db)))

(reg-event-db
 :demo/time2
 (fn [db [_ t]]
   (assoc db :time2 t)))

(reg-sub
 :demo/time2
 (fn [db _]
   (:time2 db)))
