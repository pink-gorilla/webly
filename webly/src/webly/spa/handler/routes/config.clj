(ns webly.spa.handler.routes.config
(:require
  [ring.util.response :refer [response]]
  [modular.webserver.middleware.api :refer [wrap-api-handler]]))

(defn create-config-routes [config]
  (let [config-handler (fn [_] (response config)) 
        config-handler-wrapped (wrap-api-handler config-handler)]
    {"config" config-handler-wrapped}))

