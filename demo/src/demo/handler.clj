(ns demo.handler
  (:require
   [clojure.string]
   [webserver.middleware.api :refer [wrap-api-handler]]
   [demo.handler.time :as time]
   [demo.handler.ping :as ping]
   [demo.handler.snippet :as snippet]
   [demo.handler.binary :as binary]))

; handlers that are used with symbol

(def time-handler (wrap-api-handler time/time-handler))
(def time-java-handler-wrapped  (wrap-api-handler time/time-java-handler))

(def ping-handler-wrapped (wrap-api-handler ping/ping-handler))
(def snippet-handler-wrapped (wrap-api-handler snippet/snippet-handler))
