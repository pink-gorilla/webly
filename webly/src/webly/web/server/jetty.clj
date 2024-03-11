(ns webly.web.server.jetty
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ; modular
   [modular.webserver.jetty :refer [run-jetty-server]]
   [modular.webserver.handler.registry :refer [handler-registry]] 
   [modular.ws.core :refer [init-ws!]]))

(defn jetty-ws-map [jetty-ws]
  (let [v  (map (fn [[route kw]]
                  [route (get @handler-registry kw)])
                jetty-ws)]
    (debug "jetty ws map:" jetty-ws)
    (into {} v)))

(defn jetty-ws-handler [jetty-ws]
  (let [conn (init-ws! :jetty)
        ws-map (jetty-ws-map jetty-ws)]
    ws-map))

(defn start-jetty
  [ring-handler config]
  (run-jetty-server ring-handler
                    (jetty-ws-handler (:jetty-ws config))
                    (assoc config :join? false)))

(defn stop-jetty
  [server]
  ;https://github.com/dharrigan/websockets/blob/master/src/online/harrigan/api/router.clj
  (info "stopping jetty-server..")
  (.stop server) ; stop is async
  (.join server)) ; so let's make sure it's really stopped!