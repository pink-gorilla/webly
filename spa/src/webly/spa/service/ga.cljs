(ns webly.spa.service.ga
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.analytics.events]))

(defn start-ga []
  (dispatch [:ga/init]))



