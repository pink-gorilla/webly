(ns webly.spa.service.config
  (:require
   [re-frame.core :refer [dispatch]]
   [webly.spa.config.events]
   [webly.spa.config.subscription]))

(defn start-config []
  (dispatch [:config/load :webly/app-after-config-load]))