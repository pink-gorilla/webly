(ns webly.spa.views
  (:require
   [reagent.dom.client :as rdom]
   [frontend.css :refer [css-loader]]
   [frontend.notification :refer [notification-container]]
   [frontend.dialog :refer [modal-container]]
   [frontend.page.viewer :refer [page-viewer]]
   [webly.spa.env :refer [get-resource-path]]))

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-app []
  [:div
   [modal-container]
   [notification-container]
   [css-loader (get-resource-path)]
   [page-viewer]])

(defn mount-app []
  (let [root (rdom/create-root (.getElementById js/document "app"))]
    (rdom/render root [webly-app])))
