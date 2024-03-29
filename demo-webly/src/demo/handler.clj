(ns demo.handler
  (:require
   [clojure.string]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [demo.handler.time :as time]
   [demo.handler.ping :as ping]
   [demo.handler.snippet :as snippet]
   [demo.handler.binary :as binary]
   [demo.handler.bidi :as bidi]))

; handlers that are used with symbol

(def time-handler (wrap-api-handler time/time-handler))

(def ping-handler-wrapped (wrap-api-handler ping/ping-handler))

(def time-java-handler-wrapped  (wrap-api-handler time/time-java-handler))
(def snippet-handler-wrapped (wrap-api-handler snippet/snippet-handler))
(def bidi-test-handler-wrapped (wrap-api-handler bidi/bidi-test-handler))