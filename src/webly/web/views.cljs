(ns webly.web.views
  (:require
   [cljs.pprint]
   [taoensso.timbre :as timbre :refer-macros [tracef debugf infof warnf errorf info]]
   [reagent.dom]
   [pinkgorilla.ui.config :refer [link-css]]
   [pinkgorilla.ui.ui.dialog :refer [modal-container]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current query-params]]))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 "Bummer, reagent-handler not found!"]
   [:p (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

(defn webly-app []
  (fn []
    (let [current-page @current] ; (:handler @current)
      [:div
       [link-css "tailwindcss/dist/tailwind.css"]
       [link-css "@fortawesome/fontawesome-free/css/all.min.css"]
       [modal-container]
       (reagent-page current-page)])))