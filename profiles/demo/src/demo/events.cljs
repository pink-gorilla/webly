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
                            (dispatch [:webly/status :running])) 5000)

   db))


(reg-event-db
 :webly/before-load
 (fn [db [_]]
   (warn "webly/before-load: customize your app for lein webly watch..")
 ))


(reg-event-db
 :oauth2/logged-in
 (fn [db [_ provider]]
   (info "logged in to: " provider)))
