(ns frontend.css.subscriptions
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [frontend.css.config :refer [css-app]]
   [webly.app.mode :refer [get-resource-path]]
   ))

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
         (get-in db [:theme])
         prefix (get-resource-path)]
     (debug "app theme link prefix: " prefix)
     (css-app prefix available current))))

(rf/reg-sub
 :css/theme-component
 (fn [db [_ component]]
   (get-in db [:theme :current component])))

