(ns webly.app.service.theme
  (:require
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [frontend.css.events]
   [frontend.css.loading]
   [frontend.css.subscriptions]))

(defn start-theme [theme]
  (dispatch [:css/init theme]))



