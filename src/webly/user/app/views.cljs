(ns webly.user.app.views
  (:require
   [cljs.pprint]
   [reagent.dom]
   [re-frame.core :refer [subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current]]
   [webly.user.status.view :refer [status-page]]
   [webly.user.css.view :refer [load-css]]
   [webly.user.dialog :refer [modal-container]]
   [webly.user.notifications.dialog :refer [notifications-container]]
   [webly.user.status.subscriptions] ; side-effects
   ))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 "Bummer, reagent-handler not found!"]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

(defn webly-page []
  (let [show? (subscribe [:webly/status-show-app])]
    (fn []
      (if @show?
        (reagent-page @current)
        [status-page]))))

(defn webly-app []
  [:div.w-full.h-full
   [modal-container]
   [notifications-container]
   [load-css]
   [webly-page]])