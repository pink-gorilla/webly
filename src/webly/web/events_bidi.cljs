(ns webly.web.events-bidi
  (:require
   [taoensso.timbre :refer-macros [info]]
   [re-frame.core :refer [reg-event-db reg-event-fx]]
   [pushy.core :as pushy]
   [webly.web.routes :refer [routes set-initial-query-params history goto!]]))

(reg-event-db
 :bidi/init
 (fn [db [_ routes-frontend routes-backend]]
   (info "bidi init ..")
   (reset! routes routes-frontend)
   (info "bidi routes-frontend are: " routes-frontend)
   (let [db (or db {})]
     (info "starting pushy")
     (pushy/start! history) ; link url => state
     (set-initial-query-params)
     (assoc-in db [:bidi] {:client routes-frontend :server routes-backend}))))

(reg-event-fx
 :bidi/goto
 (fn [_ [_ handler & params]]
   (info "bidi goto handler: " handler " params: " params)
   (if (> (count params) 0)
     (do (info "goto! (params): "  (concat [handler] params))
         (apply goto! (concat [handler] params)))
     (do (info "goto! (no-params)" handler)
         (goto! handler)))))