(ns webly.user.css.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [clojure.string :as str]
   [re-frame.core :refer [reg-event-db]]))

(reg-event-db
 :css/init
 (fn [db [_]]
   (info "css init: ")
   (assoc db :theme {:available {}
                     :current {}})
   db))

(reg-event-db
 :css/add-components
 (fn [db [_ components components-default-config]]
   (info "css add-component: ")
   (let [{:keys [available current]
          :or {available {}
               current {}}}
         (:theme db)
         theme {:available (merge available components)
                :current (merge current components-default-config)}]
     (assoc db :theme theme))))

(reg-event-db
 :css/set-theme-component
 (fn [db [_ component theme]]
   (assoc-in db [:theme :current component] theme)))




