(ns webly.user.notifications.dialog
  (:require
   [clojure.string :as str]
   [re-frame.core :as rf :include-macros true]
   [webly.user.notifications.core :refer [notification-types]]
   [webly.user.notifications.subscriptions] ; side-effects
   [webly.user.notifications.events] ; side-effects
   ))

;; stolen from:
;; https://github.com/baskeboler/cljs-karaoke-client/blob/master/src/main/cljs_karaoke/notifications.cljs


;.notification:hover {
;    opacity: 1;
;} 

(defn type-css-class [notification-type]
  (assert (notification-types notification-type))
  (case notification-type
    :danger "bg-red-100 border-l-4 border-red-500 text-red-700 p-4"
    :warning "bg-orange-100 border-l-4 border-orange-500 text-orange-700 p-4"
    :info "bg-blue-100 border-l-4 border-blue-500 text-blue-700 p-4"
    "bg-orange-100 border-l-4 border-orange-500 text-orange-700 p-4"))

(defn notification-component [n]
  [:div
   {:key (str "notification-" (:id n))
    :class (type-css-class (:type n))
    :style {:opacity 0.8
            :margin-bottom "0.5em !important"
            :hover {:opacity 1.0}}
    :role "alert"}
   [:button
    {:on-click #(rf/dispatch [:notification/dismiss (:id n)])}
    [:i.fas.fa-trash.mr-3]]
   (:text n)])

(defn ^:export notifications-container []
  (let [nots-subs (rf/subscribe [:notifications])]
    [:div {:style {:position "fixed"
                   :width "calc(100vw - 3em)"
                   :top "6em"
                   :right "1em"
                   :z-index 300;
                   :overflow "hidden"}}
     (when (not-empty @nots-subs)
       (doall
        (for [n @nots-subs]
          ^{:key (str "notify-" (:id n))}
          [notification-component n])))]))