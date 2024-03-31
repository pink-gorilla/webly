(ns webly.app.service.ga
  (:require
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [frontend.analytics.events]))

(defn start-ga []
   (dispatch [:ga/init]))



