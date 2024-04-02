(ns webly.spa.service.keybindings
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.keybindings.events]))

(defn start-keybindings [keybindings]
  (dispatch [:keybindings/init keybindings]))






