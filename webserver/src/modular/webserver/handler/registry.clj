(ns modular.webserver.handler.registry)

(defonce handler-registry
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! handler-registry assoc key handler))