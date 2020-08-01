(ns webly.web.views
  (:require
   [cljs.pprint]
   [reagent.dom]
   [webly.config :refer [webly-config link-css]]
   [webly.user.dialog :refer [modal-container]]
   [webly.user.notifications.dialog :refer [notifications-container]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current query-params]]))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 "Bummer, reagent-handler not found!"]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

(defn webly-app []
  (fn []
    (let [current-page @current
          {:keys [css-links]} @webly-config]
      [:div
       (doall (map-indexed
               (fn [i l] ^{:key i} [link-css l])
               css-links))
       ;[link-css "tailwindcss/dist/tailwind.css"]
       ;[link-css "@fortawesome/fontawesome-free/css/all.min.css"]
       [modal-container]
       [notifications-container]
       (reagent-page current-page)])))