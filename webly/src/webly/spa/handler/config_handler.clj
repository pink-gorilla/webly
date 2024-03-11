(ns webly.spa.handler.config-handler
(:require
  [ring.util.response :refer [response]]
  [modular.config :refer [config-atom]]
  [modular.webserver.middleware.api :refer [wrap-api-handler]]))


(defn config-handler
  [_]
  (response @config-atom))

(def config-handler-wrapped
  (wrap-api-handler config-handler))


