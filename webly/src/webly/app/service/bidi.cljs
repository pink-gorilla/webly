(ns webly.app.service.bidi
  (:require
   [re-frame.core :refer [dispatch]]
   [frontend.routes.events]
   [webly.app.mode :refer [get-routing-path]]
   ))


(defn make-routes-frontend [rpath user-routes-app]
  [rpath user-routes-app])

(defn start-bidi [user-routes-app]
  (let [rpath (get-routing-path)
        routes-frontend (make-routes-frontend rpath user-routes-app)]
    (dispatch [:bidi/init routes-frontend])))