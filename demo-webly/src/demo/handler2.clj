(ns demo.handler2
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [demo.handler.time :as time]
   [demo.handler.ping :as ping]
   [demo.handler.snippet :as snippet]
   [demo.handler.binary :as binary]
   [demo.handler.bidi :as bidi]))

; handler that use the registry

(add-ring-handler :api/time-java (wrap-api-handler time/time-java-handler))
(add-ring-handler :api/ping (wrap-api-handler ping/ping-handler))
(add-ring-handler :api/binary binary/binary-handler)
(add-ring-handler :api/snippet (wrap-api-handler snippet/snippet-handler))
(add-ring-handler :api/bidi-test (wrap-api-handler bidi/bidi-test-handler))

