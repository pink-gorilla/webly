(ns webly.app.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe]]
   [frontend.css.view :refer [load-css]]
   [frontend.notifications.dialog :refer [notifications-container]]
   [frontend.dialog :refer [modal-container]]
   [frontend.page :refer [reagent-page]]
   [frontend.routes :refer [current]]
   [webly.app.status.page :refer [status-page]]))

(defonce generation (r/atom 1))
(defn page-viewer [current]
  ^{:key [@generation @current]} [reagent-page @current]) ; multimethod fix

(defn refresh-page
  "used in goldly for dynamic reloading when page source was changed"
  []
  (swap! generation inc))

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-page []
  (let [show? (subscribe [:webly/status-show-app])]
    (fn []
      (if @show?
        [page-viewer current]
        [status-page]))))

(defn webly-app []
  [:div; .w-full.h-full
   [modal-container]
   [notifications-container]
   [load-css]
   [webly-page]])