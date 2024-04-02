(ns webly.spa.events
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [reg-event-db dispatch]]))

(reg-event-db
 :webly/start-default
 (fn [db [_]]
   ; this event exists, so that apps that dont have on-init code dont have to
   ; raise :webly/status :running   
   (info "webly init-default. (to configure your app start set :start-user-app in config)")
    ; simulate a slow bundle load time, so we can see the ui
   (dispatch [:webly/status :running])
   db))