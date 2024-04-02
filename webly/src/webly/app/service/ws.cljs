(ns webly.app.service.ws
  (:require
   [re-frame.core :refer [dispatch]]
   [modular.ws.events]
   [modular.ws.core]))

(defn start-ws []
  (dispatch [:ws/init]))


