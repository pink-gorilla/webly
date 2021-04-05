(ns demo.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 :demo/start
 (fn [db [_]]
   (info "starting demo app..")
   db))