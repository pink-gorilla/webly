(ns webly.app.service.bidi
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.routes.events]))


(defn make-routes-frontend [user-routes-app]
  ["/" user-routes-app])

(defn start-bidi [user-routes-app]
  (let [routes-frontend (make-routes-frontend user-routes-app)]
    (dispatch [:bidi/init routes-frontend])))