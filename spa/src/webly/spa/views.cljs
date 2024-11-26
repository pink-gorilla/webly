(ns webly.spa.views
  (:require
   [re-frame.core :refer [subscribe]]
   [frontend.css.view :refer [load-css]]
   [frontend.notification :refer [notification-container]]
   [frontend.dialog :refer [modal-container]]
   [frontend.page.viewer :refer [page-viewer]]
   [webly.spa.loader.page :refer [loader-page]]))

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-page []
  (let [show? (subscribe [:webly/status-show-app])]
    (fn []
      (if @show?
        [page-viewer]
        [loader-page]))))

(defn webly-app []
  [:div
   [modal-container]
   [notification-container]
   [load-css]
   [webly-page]])