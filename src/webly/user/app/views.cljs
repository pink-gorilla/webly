(ns webly.user.app.views
  (:require
   [cljs.pprint]
   [reagent.dom]
   [re-frame.core :refer [subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current query-params]]
   [webly.user.config.core :refer [link-css]]
   [webly.user.dialog :refer [modal-container]]
   [webly.user.notifications.dialog :refer [notifications-container]]))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 "Bummer, reagent-handler not found!"]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

(defn css [css-links]
  (println "css links:" css-links)
  [:div
   (doall (map-indexed
           (fn [i l] ^{:key i} [link-css l])
           css-links))
       ;[link-css "tailwindcss/dist/tailwind.css"]
       ;[link-css "@fortawesome/fontawesome-free/css/all.min.css"]
   ])

(defn webly-app []
  (let [config (subscribe [:webly/config])]
    (fn []
      (let [current-page @current
            {:keys [css-links]} @config]
        [:div
         [css css-links]
         [modal-container]
         [notifications-container]
         (reagent-page current-page)]))))