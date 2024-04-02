(ns webly.spa.config.subscription
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :webly/config
 (fn [db _]
   (get-in db [:config])))
