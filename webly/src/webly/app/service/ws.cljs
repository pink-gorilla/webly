(ns webly.app.service.ws
  (:require
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [modular.ws.events]
   [modular.ws.core]))

(defn start-ws []
  (dispatch [:ws/init]))


