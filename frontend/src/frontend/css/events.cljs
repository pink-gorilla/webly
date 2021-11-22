(ns frontend.css.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]))

(rf/reg-event-db
 :css/init
 (fn [db [_]]
   (let [theme (get-in db [:config :webly :theme])
         {:keys [available current]} (or theme {})]
     (info "css init: " theme)
     (rf/dispatch [:css/add-components available current])
     db)))

(rf/reg-event-db
 :css/add-components
 (fn [db [_ components components-default-config]]
   (info "css add-component: " components-default-config)
   (let [{:keys [theme]
          :or {theme {}}} db
         {:keys [available current]
          :or {available {}
               current {}}} theme
         theme {:available (merge available components)
                :current (merge current components-default-config)}]
     (assoc db :theme theme))))

(rf/reg-event-db
 :css/set-theme-component
 (fn [db [_ component theme]]
   (assoc-in db [:theme :current component] theme)))




