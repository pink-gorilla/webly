(ns frontend.settings.subscriptions
  (:require
   [clojure.string :as str]
   [taoensso.timbre :refer-macros [info warn error]]
   [re-frame.core :refer [reg-sub]]
   [webly.app.mode :refer [get-resource-path]]
   ))

(reg-sub
 :settings
 (fn [db _]
   (get-in db [:settings])))

(reg-sub
 :prefix
 (fn [db _]
   (let [prefix (get-resource-path)]
     (if (str/blank? prefix)
       (do (error "prefix is blank - returning /r/")
           "/r/")
       prefix))))
