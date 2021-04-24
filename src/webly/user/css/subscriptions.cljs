(ns webly.user.css.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]
   [webly.user.css.config :refer [css-app]]))

(reg-sub
 :css/app-theme-links
 (fn [db [_]]
   (let [{:keys [available current]
          :or {available {}
               current {}}}
         (get-in db [:theme])]
     (css-app available current))))

(reg-sub
 :css/theme-component
 (fn [db [_ component]]
   (get-in db [:theme :current component])))

