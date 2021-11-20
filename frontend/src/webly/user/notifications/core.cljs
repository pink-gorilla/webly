(ns webly.user.notifications.core
  (:require
   [re-frame.core :refer [dispatch]]))

;; stolen from:
;; https://github.com/baskeboler/cljs-karaoke-client/blob/master/src/main/cljs_karaoke/notifications.cljs

; todo: make it more tailwind like
; ; https://www.creative-tim.com/learning-lab/tailwind-starter-kit/documentation/react/alerts

(def notification-types #{:info :warning :error})

(defn notification
  ([hiccup]
   (notification hiccup :info 5000))
  ([type hiccup]
   (notification hiccup type 5000))
  ([type hiccup ms]
   (assert (notification-types type))
   {:id (random-uuid)
    :type type
    :hiccup hiccup
    :ms ms}))

(defn ^:export add-notification
  ([hiccup]
   (add-notification :info hiccup 5000))
  ([type hiccup]
   (add-notification type hiccup 5000))
  ([type hiccup ms]
   (let [n (notification type hiccup ms)]
     (dispatch [:notification/add n])
     (:id n))))
