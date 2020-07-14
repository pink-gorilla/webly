(ns webly.user.notifications.events
  (:require
   [taoensso.timbre :refer-macros [error]]
   [re-frame.core :as rf :include-macros true :refer [dispatch]]
   ;[day8.re-frame.tracing :refer-macros [fn-traced]]
   [webly.user.notifications.core :refer [notification]]))

;; stolen from:
;; https://github.com/baskeboler/cljs-karaoke-client/blob/master/src/main/cljs_karaoke/events/notifications.cljs

(rf/reg-event-fx
 :notification/add
 (fn ; fn-traced
   [{:keys [db]} [_ n]]
   {:db (-> db (update :notifications conj n))
    :dispatch-later [{:ms 5000
                      :dispatch [:notification/dismiss (:id n)]}]}))

(rf/reg-event-fx
 :notification/show
 (fn ; fn-traced
   [_ [_ text type]]
   (let [n (notification (or type :primary) text)]
     (dispatch [:notification/add n])
     nil)))

(rf/reg-event-db
 :notification/dismiss
 (fn ; fn-traced
   [db [_ notification-id]]
   (-> db
       (update :notifications (fn [notis]
                                (filterv
                                 #(not= notification-id (:id %))
                                 notis))))))

(rf/reg-event-db
 :process-error-response
 (fn [db [_ location response]]
   (error "ERROR RESPONSE: " response)
   (dispatch [:notification/add
              (notification :warning
                            (str location " Error: " (:status-text response) " (" (:status response) ")"))])
   db))
