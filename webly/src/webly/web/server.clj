(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ; modular
   [modular.config :refer [get-in-config]] ;; BAD
   [webly.web.server.jetty :as jetty]
   [webly.web.server.httpkit :as httpkit]))

(defn start2 [config ring-handler server-type]
  (let [server (case server-type
                 :jetty (jetty/start-jetty ring-handler config)
                  ;:undertow (run-undertow-server ring-handler port host api)
                 :httpkit (httpkit/start-httpkit ring-handler config)
                  ;:shadow (run-shadow-server)
                 (do (error "start-server failed: server type not found: " type)
                     nil))]
    {:server-type server-type
     :server server}))

(defn start
  ([ring-handler server-type]
   (let [config (get-in-config [:webly/web-server])] ;; BAD!!!
     (start2 config ring-handler server-type)))
  ([config ring-handler server-type]
   (start2 config ring-handler server-type)))



(defn stop [{:keys [server server-type]}]
  (when server
    (case server-type
      :jetty (jetty/stop-jetty server)
      :httpkit (httpkit/stop-httpkit server)
      (info "there was no server started."))))
