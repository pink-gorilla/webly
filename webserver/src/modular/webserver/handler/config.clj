(ns modular.webserver.handler.config
  (:require
   [ring.util.response :refer [response]]
   [modular.config :refer [config-atom]]
   ;[webly.web.middleware :refer [wrap-api-handler]]
   ))

(defn config-handler
  [_]
  (response @config-atom))
