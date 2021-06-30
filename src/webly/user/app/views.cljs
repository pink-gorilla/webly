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

(defn page-viewer [current]
  ^{:key @current} [reagent-page @current]) ; multimethod fix

; https://stackoverflow.com/questions/33299746/why-are-multi-methods-not-working-as-functions-for-reagent-re-frame
; ^{:key @current-route} [pages @current-route]

(defn webly-page []
  (let [show? (subscribe [:webly/status-show-app])]
    (fn []
      (if @show?
        ;(reagent-page @current)
        [page-viewer current]
        [status-page]))))

(defn webly-app []
  [:div; .w-full.h-full
   [modal-container]
   [notifications-container]
   [load-css]
   [webly-page]])