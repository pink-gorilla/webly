(ns webly.app.service.ga
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.analytics.events]))

(defn start-ga []
  (dispatch [:ga/init]))



