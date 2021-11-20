(ns webly.user.app.views
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current]]
   [webly.user.status.view :refer [status-page]]
   [webly.user.css.view :refer [load-css]]
   [webly.user.dialog :refer [modal-container]]
   [webly.user.notifications.dialog :refer [notifications-container]]))

(defn available-pages []
  (->> (methods reagent-page)
       keys
       (remove #(= :default %))
       (into [])))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 (str "page " (:handler @current) " - not found!")]
   [:p "Available pages: " (pr-str (available-pages))]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

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