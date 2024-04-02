(ns webly.app.service.config
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.config.events]
   [frontend.config.subscription]))

(defn start-config []
  (dispatch [:config/load :webly/app-after-config-load]))