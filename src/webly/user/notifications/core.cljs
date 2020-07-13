(ns webly.user.notifications.core
  (:require
   [re-frame.core :refer [dispatch]]))

;; stolen from:
;; https://github.com/baskeboler/cljs-karaoke-client/blob/master/src/main/cljs_karaoke/notifications.cljs

(def notification-types #{:info :warning :danger :primary :success})

(defn notification
  [type text]
  (assert (notification-types type))
  {:id (random-uuid)
   :type type
   :text text})

(defn ^:export add-notification
  ([text]
   (add-notification :primary text))
  ([type text]
   (let [n (notification type text)]
     (dispatch [:notification/add n])
     (:id n))))
