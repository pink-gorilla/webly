(ns webly.user.analytics.events
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :refer [reg-event-db]]
   [webly.user.analytics.google-tag :refer [send-event]]
   ;[district0x.re-frame.google-analytics-fx]
   ))

(reg-event-db
 :ga/init
 (fn [db [_]]
   (let [{:keys [enabled id debug?]} (get-in db [:config :google-analytics])]
     (if enabled
       (do
         (info "ga init id: " id " debug?: " debug?)
         ;(ga-init id debug?)
         )
       (warn "google analytics disabled.")))
   db))

(reg-event-db
 :ga/event
 (fn [db [_ category action label]]
   (let [{:keys [enabled]} (get-in db [:config :google-analytics])]
     (when enabled
       (info "ga event" category)
       ;(gtag "event" (name category)); label value (clj->js fields-object)
       (send-event category {:action action :label label})))
   db))

#_(reg-fx
   :ga/event
   (fn [[category action label value fields-object]]
     (when *enabled*
       (js/ga "send" "event" category action label value (clj->js fields-object)))))