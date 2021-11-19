(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.repl]
   ; modular
   [modular.config :refer [get-in-config]]
   [modular.webserver.jetty :refer [run-jetty-server]]
   [modular.webserver.handler.registry :refer [handler-registry]]
   ; webly
   ;[webly.web.middleware :refer [wrap-webly]]
   [webly.ws.core :refer [init-ws!]]
   ;[webly.ws.handler :refer [ws-handshake-handler]]
   ;[webly.web.hooks]
   ))
(defn stop-server []
  (warn "stop-server ..")
  (Thread/sleep 100))

(defn stop-server-repl [_]
  (warn "stop-server-repl ..")
  (Thread/sleep 100)
  (System/exit 0))

(defn jetty-ws-map []
  (let [jetty-ws (get-in-config [:jetty-ws])
        v  (map (fn [[route kw]]
                  [route (get @handler-registry kw)])
                jetty-ws)]
    (debug "jetty ws map:" jetty-ws)
    (into {} v)))

(defn jetty-ws-handler []
  (let [conn (init-ws! :jetty)
        ws-map (jetty-ws-map)]
    ws-map))

(defn run-server [ring-handler profile]
  (let [{:keys [type api wrap-handler-reload]} (get-in profile [:server])
        jetty-config (get-in-config [:web-server])
        jetty-config (if api
                       (do  (debug "using web-server-api")
                            (assoc jetty-config :join? false))
                       (assoc jetty-config :join? true))]
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-server))
    (clojure.repl/set-break-handler! stop-server-repl)
    (case type
      :jetty (run-jetty-server ring-handler
                               (jetty-ws-handler)
                               jetty-config)
      ;:undertow (run-undertow-server ring-handler port host api)
      ;:httpkit (run-httpkit-server ring-handler port host api)
      ;:shadow (run-shadow-server)
      (error "run-server failed: server type not found: "))))
