(ns webly.user.markdown.subscriptions
  (:require
   [clojure.walk]
   [taoensso.timbre :as timbre :refer [debug info]]
   [re-frame.core :refer [reg-sub dispatch]]))

(reg-sub
 :markdown/showing-file
 (fn [db [_]]
   (get-in db [:markdown :showing :file])))

(reg-sub
 :markdown/showing-doc
 (fn [db [_]]
   (get-in db [:markdown :showing :doc])))

(reg-sub
 :markdown/available
 (fn [db [_]]
   (get-in db [:markdown :available])))