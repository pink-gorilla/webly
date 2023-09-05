(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ; modular
   [modular.config :refer [get-in-config]]
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

(defn stop-jetty
  [server]
  ;https://github.com/dharrigan/websockets/blob/master/src/online/harrigan/api/router.clj
  (info "stopping jetty-server..")
  (.stop server) ; stop is async
  (.join server)) ; so let's make sure it's really stopped!

(defn start2 [config ring-handler server-type]
  (let [server (case server-type
                 :jetty (run-jetty-server ring-handler
                                          (jetty-ws-handler (:jetty-ws config))
                                          (assoc config :join? false))
                  ;:undertow (run-undertow-server ring-handler port host api)
                 :httpkit (let [run-server (requiring-resolve 'modular.webserver.httpkit/run-server)]
                            (run-server ring-handler config)
                            (init-ws! :httpkit))
                  ;:shadow (run-shadow-server)
                 (do (error "start-server failed: server type not found: " type)
                     nil))]
    {:server-type server-type
     :server server}))

(defn start 
  ([ring-handler server-type]
    (let [config (get-in-config [:webly/web-server])]
      (start2 config ring-handler server-type)))
  ([config ring-handler server-type]
     (start2 config ring-handler server-type)))


(defn stop-httpkit [server]
  (info "stopping httpkit server..")
  ;(server) ; Immediate shutdown (breaks existing reqs)
  ;; Graceful shutdown (wait <=100 msecs for existing reqs to complete):
  (server :timeout 100))

(defn stop [{:keys [server server-type]}]
  (when server
    (case server-type
      :jetty (stop-jetty server)
      :httpkit (stop-httpkit server)
      (info "there was no server started."))))
