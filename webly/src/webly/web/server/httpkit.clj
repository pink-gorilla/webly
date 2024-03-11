(ns webly.web.server.httpkit
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.ws.core :refer [init-ws!]]))

(defn start-httpkit
  [ring-handler config]
  (let [run-server (requiring-resolve 'modular.webserver.httpkit/run-server)]
    (run-server ring-handler config)
    (init-ws! :httpkit)))

(defn stop-httpkit [server]
  (info "stopping httpkit server..")
  ;(server) ; Immediate shutdown (breaks existing reqs)
  ;; Graceful shutdown (wait <=100 msecs for existing reqs to complete):
  (server :timeout 100))