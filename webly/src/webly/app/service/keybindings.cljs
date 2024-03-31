(ns webly.app.service.keybindings
  (:require
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [frontend.keybindings.events]))



(defn start-keybindings [keybindings]
  (dispatch [:keybindings/init keybindings]))






