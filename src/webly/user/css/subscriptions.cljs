(ns webly.user.css.subscriptions
  (:require
   [re-frame.core :as rf]
   [webly.user.css.config :refer [css-app]]))

(rf/reg-sub
 :css/theme
 (fn [db [_]]
   (let [{:keys [available current]
          :or {available {}
               current {}}}
         (get-in db [:theme])]
     {:available available
      :current current})))

(rf/reg-sub
 :css/app-theme-links
 (fn [db [_]]
   (let [{:keys [available current]
          :or {available {}
               current {}}}
         (get-in db [:theme])]
     (css-app available current))))

(rf/reg-sub
 :css/theme-component
 (fn [db [_ component]]
   (get-in db [:theme :current component])))

