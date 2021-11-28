(ns frontend.analytics.events
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :refer [reg-event-db]]
   [frontend.analytics.google-tag :refer [send-event]]))

(reg-event-db
 :ga/init
 (fn [db [_]]
   (let [{:keys [enabled id debug?]} (get-in db [:config :google-analytics])]
     (if enabled
       (do
         (info "ga init id: " id " debug?: " debug?)
         ;(ga-init id debug?)
         )
       (debug "google analytics disabled.")))
   db))

(reg-event-db
 :ga/event
 (fn [db [_ {:keys [category action label value]}]]
   (let [{:keys [enabled]} (get-in db [:config :google-analytics])
         data {:event_category category
               :event_label label
               :value value}]
     (when enabled
       (info "ga send event" category)
       ;(gtag "event" (name category)); label value (clj->js fields-object)
       (send-event action data)))
   db))
