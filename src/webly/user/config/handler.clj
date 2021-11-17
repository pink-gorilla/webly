(ns webly.user.config.handler
  (:require
   [modular.webserver.handler.config :refer [config-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]))

(add-ring-handler :webly/config (wrap-api-handler config-handler))