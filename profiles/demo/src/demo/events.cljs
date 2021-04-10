(ns demo.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db
 :demo/start
 (fn [db [_]]
   (info "starting demo app..")
   (dispatch [:ga/event {:category "webly-demo" :action "started" :label 77 :value 13}])
   (.setTimeout js/window (fn []
                            (info "webly demo started.")
                            (dispatch [:webly/status :running])) 5000)

   db))

