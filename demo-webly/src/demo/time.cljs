(ns demo.time
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db reg-sub]]))

(reg-event-db
 :demo/time
 (fn [db [_ t]]
   (assoc db :time t)))

(reg-sub
 :demo/time
 (fn [db _]
   (:time db)))

