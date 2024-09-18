(ns webly.spa.tenx.events-off
  "reframe events related to 10x
   this ns gets changed in in shadow cljs config with 
   :ns-aliases to webly.spa.tenx.events
   note: calling ns 10x does lead to errors (at runtime in the bundle) "
  (:require
   [taoensso.timbre :refer-macros [info]]
   [re-frame.core :refer [reg-event-db]]
   [frontend.notification :refer [show-notification]]))

(reg-event-db
 :reframe10x-init
 (fn [db _]
   (info "10x not included in bundle")
   db))

(reg-event-db
 :reframe10x-toggle
 (fn [db _]
   (show-notification :error "tenx not included in bundle!")
   db))