(ns demo.handler
  (:require
   [clojure.string]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [demo.handler.time :as time]))

; handlers that are used with symbol

(def time-handler (wrap-api-handler time/time-handler))



