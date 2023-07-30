(ns frontend.routes.events
  (:require
   [taoensso.timbre :refer-macros [debug info]]
   [re-frame.core :as rf]
   [pushy.core :as pushy]
   [frontend.routes :refer [routes history goto! goto-route! nav!]])) ;set-initial-query-params

(rf/reg-event-db
 :bidi/init
 (fn [db [_ routes-frontend routes-backend]]
   (info "bidi init ..")
   (reset! routes routes-frontend)
   (info "bidi routes-frontend are: " routes-frontend)
   (let [db (or db {})]
     (info "starting pushy")
     (pushy/start! history) ; link url => state
     (assoc-in db [:bidi] {:client routes-frontend :server routes-backend}))))

(rf/reg-event-fx
 :bidi/goto-route
 (fn [_ [_ match]]
   (debug "bidi/goto-route: " match)
   (goto-route! match)
   nil))

(rf/reg-event-fx
 :bidi/goto
 (fn [_ [_ handler_or_url & params]]
   (if (string? handler_or_url)
     (nav! handler_or_url)
     (if (> (count params) 0)
       (do (debug "bidi/goto! (params): "  (concat [handler_or_url] params))
           (apply goto! (concat [handler_or_url] params)))
       (do (debug "bidi/goto! (no-params)" handler_or_url)
           (goto! handler_or_url))))
   nil))

(rf/reg-sub
 :webly/routes
 (fn [db _]
   (get-in db [:bidi])))
