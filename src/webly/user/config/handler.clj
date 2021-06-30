(ns webly.user.config.handler
  (:require
   [ring.util.response :refer [response]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.config :refer [config-atom]]))

(defn config-handler
  [_]
  (response @config-atom))

(add-ring-handler :webly/config (wrap-api-handler config-handler))