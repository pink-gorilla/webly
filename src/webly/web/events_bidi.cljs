(ns webly.web.events-bidi
  (:require
   [taoensso.timbre :refer-macros [info]]
   [re-frame.core :refer [reg-event-db reg-event-fx]]
   [pushy.core :as pushy]
   [webly.web.routes :refer [routes set-initial-query-params history goto!]]))

(reg-event-db
 :bidi/init
 (fn [db [_ new-routes]]
   (info "bidi init ..")
   (reset! routes new-routes)
   (info "bidi routes are: " new-routes)
   (let [db (or db {})]
     (info "starting pushy")
     (pushy/start! history) ; link url => state
     (set-initial-query-params)
     (assoc db :bidi new-routes))))

(reg-event-fx
 :bidi/goto
 (fn [_ [_ handler & params]]
   (info "bidi goto handler: " handler " query-params: " params)
   (if (> (count params) 0)
     (do (info "p: "  (concat [handler] params))
         (apply goto! (concat [handler] params)))
     (do (info "goto! no-qp")
         (goto! handler)))))