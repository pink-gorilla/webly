(ns webly.user.config.handler
  (:require
   [ring.util.response :refer [response]]
   [modular.config :refer [config-atom]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]))

(defn config-handler
  [_]
  (response @config-atom))

(add-ring-handler :webly/config (wrap-api-handler config-handler))