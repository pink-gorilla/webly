(ns webly.app.tenx.events-on
  "reframe events related to 10x
   this ns gets changed in in shadow cljs config with
   :ns-aliases to webly.app.tenx.events
  note: calling ns 10x does lead to errors (at runtime in the bundle) "
  (:require
   [taoensso.timbre :refer-macros [info]]
   [clojure.string]
   [re-frame.core :refer [reg-event-db]]
   [day8.re-frame-10x]
   [day8.re-frame-10x.preload] ; side effects
   ))

(reg-event-db
 :reframe10x-init
 (fn [db _]
   (info "hiding re-frame-10x panel")
   (.setItem js/localStorage "day8.re-frame-10x.show-panel" "false")
   (day8.re-frame-10x/show-panel! false)
   (assoc-in db [:dev :ten10x-visible?] false)))

(reg-event-db
 :reframe10x-toggle
 (fn [db _]
   (let [visible (not (get-in db [:dev :ten10x-visible?]))]
     ; https://github.com/day8/re-frame-10x/pull/210
     (info "reframe-10x toggle. visible: " visible)
     (day8.re-frame-10x/show-panel! visible)
 ; _ (.setItem js/localStorage "day8.re-frame-10x.show-panel" (str visible))
     (assoc-in db [:dev :ten10x-visible?] visible))))


