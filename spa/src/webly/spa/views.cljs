(ns webly.spa.views
  (:require
   [frontend.css.view :refer [load-css]]
   [frontend.notification :refer [notification-container]]
   [frontend.dialog :refer [modal-container]]
   [frontend.page.viewer :refer [page-viewer]]))

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-app []
  [:div
   [modal-container]
   [notification-container]
   ;[load-css]
   [page-viewer]])