(ns webly.user.notifications.events
  (:require
   [taoensso.timbre :refer-macros [error]]
   [re-frame.core :as rf :include-macros true :refer [dispatch]]
   [webly.user.notifications.core :refer [notification]]))

;; stolen from:
;; https://github.com/baskeboler/cljs-karaoke-client/blob/master/src/main/cljs_karaoke/events/notifications.cljs

(rf/reg-event-fx
 :notification/add
 (fn [{:keys [db]} [_ {:keys [id ms] :as n}]]
   (if (= ms 0)
     {:db (-> db (update :notifications conj n))}
     {:db (-> db (update :notifications conj n))
      :dispatch-later [{:ms ms
                        :dispatch [:notification/dismiss id]}]})))

(rf/reg-event-fx
 :notification/show
 (fn [_ [_ hiccup type]]
   (let [n (notification type hiccup)]
     (dispatch [:notification/add n])
     nil)))

(rf/reg-event-db
 :notification/dismiss
 (fn [db [_ notification-id]]
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
