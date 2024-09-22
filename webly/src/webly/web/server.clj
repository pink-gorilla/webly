(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.web.server.jetty :as jetty]
   [webly.web.server.httpkit :as httpkit]))

(defn start [webserver-config ring-handler websocket server-type]
  (let [server (case server-type
                 :jetty (jetty/start-jetty ring-handler websocket webserver-config)
                 :httpkit (httpkit/start-httpkit ring-handler webserver-config)
                  ;:shadow (run-shadow-server)
                 (do (error "start-server failed: server type not found: " type)
                     nil))]
    {:server-type server-type
     :server server}))

(defn stop [{:keys [server server-type]}]
  (when server
    (case server-type
      :jetty (jetty/stop-jetty server)
      :httpkit (httpkit/stop-httpkit server)
      (info "there was no server started."))))
