(ns frontend.settings.subscriptions
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer-macros [info warn error]]
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :settings
 (fn [db _]
   (get-in db [:settings])))



(reg-sub
 :prefix
 (fn [db _]
   (let [prefix (get-in db [:settings :prefix])]
     (if (str/blank? prefix)
       (do (error "prefix is blank - returning /r/")
           "/r/")
       prefix))))
