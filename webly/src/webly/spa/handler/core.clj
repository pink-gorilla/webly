(ns webly.spa.handler.core
  (:require 
    [webly.spa.handler.handler :refer [make-handler]]
    [webly.spa.html.handler :refer [app-handler]]
    [webly.spa.handler.routes :refer [make-routes-frontend make-routes-backend]]))

(defn create-ring-handler
  "creates a ring-handler
   uses configuration in webly-config to do so
   the def statement defines a variable in this ns. This is used by shadow-cljs to resolve the handler.
   "
  [routes]
  (let [routes-backend (make-routes-backend (:app routes) (:api routes))
        routes-frontend (make-routes-frontend (:app routes))
        h (make-handler app-handler routes-backend routes-frontend)]
    {:routes {:frontend routes-frontend :backend routes-backend}
     :handler h}))


