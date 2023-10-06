(ns demo.events
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db
 :demo/start
 (fn [db [_]]
   (info "starting demo app..")
   (dispatch [:ga/event {:category "webly-demo" :action "started" :label 77 :value 13}])
   ; simulate a slow bundle load time, so we can see the ui
   (.setTimeout js/window (fn []
                            (info "webly demo started.")
                            (dispatch [:webly/status :running])) 50)

   db))

(reg-event-db
 :webly/before-load
 (fn [db [_]]
   (warn "add reframe event :webly/before-load for custom reloading during development")))
