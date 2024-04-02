(ns webly.spa.service.theme
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.css.events]
   [frontend.css.loading]
   [frontend.css.subscriptions]))

(defn start-theme [theme]
  (dispatch [:css/init theme]))



