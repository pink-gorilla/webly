(ns webly.user.css.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [clojure.string :as str]
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 :css/init
 (fn [db [_]]
   (let [theme (get-in db [:config :webly :themes])
         {:keys [available current]
          :or {available {}
               current {}}}
         (or theme {})
         theme-safe {:available available
                     :current current}]
     (info "css init: " theme-safe)
     (assoc db :theme theme-safe)
     db)))

(reg-event-db
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

(reg-event-db
 :css/set-theme-component
 (fn [db [_ component theme]]
   (assoc-in db [:theme :current component] theme)))




