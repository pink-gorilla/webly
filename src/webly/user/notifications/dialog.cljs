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

(defn type-css-class [notification-type]
  (assert (notification-types notification-type))
  (let [notification-type (or notification-type :info)
        n-class (case notification-type
                  :error "bg-red-100 border-l-4 border-red-500 text-red-700 p-4"
                  :warning "bg-yellow-100 border-l-4 border-yellow-500 text-yellow-700 p-4"
                  :info "bg-blue-100 border-l-4 border-blue-500 text-blue-700 p-4")]
    (str "notification " n-class)))

(defn notification-component [{:keys [id type hiccup]}]
  [:div
   {:key (str "notification-" id)
    :class (type-css-class type)
    :role "alert"}
   [:button
    {:on-click #(rf/dispatch [:notification/dismiss id])}
    [:i.fas.fa-trash.mr-3]]
   hiccup])

(defn ^:export notifications-container []
  (let [nots-subs (rf/subscribe [:notifications])]
    [:div {:style {:position "fixed"
                   :width "calc(100vw - 3em)"
                   :top "6em"
                   :right "1em"
                   :z-index 300;
                   :overflow "hidden"}}
     [:style (str ".notification {"
                  "opacity: 0.8; "
                  "margin-bottom 0.5em !important; "
                  "} "
                  ".notification:hover { "
                  "opacity: 1; "
                  "} ")]
     (when (not-empty @nots-subs)
       (doall
        (for [n @nots-subs]
          ^{:key (str "notify-" (:id n))}
          [notification-component n])))]))