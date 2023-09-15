(ns webly.app.views
  (:require
   [re-frame.core :refer [subscribe]]
   [frontend.css.view :refer [load-css]]
   [frontend.notifications.dialog :refer [notifications-container]]
   [frontend.dialog :refer [modal-container]]
   [frontend.page.viewer :refer [page-viewer]]
   [webly.app.status.page :refer [status-page]]))

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-page []
  (let [show? (subscribe [:webly/status-show-app])]
    (fn []
      (if @show?
        [page-viewer]
        [status-page]))))

(defn webly-app []
  [:div; .w-full.h-full
   [modal-container]
   [notifications-container]
   [load-css]
   [webly-page]])