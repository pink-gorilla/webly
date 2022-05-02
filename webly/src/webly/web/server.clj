(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   ; modular
   [modular.config :refer [get-in-config]]
   [modular.webserver.jetty :refer [run-jetty-server]]
   [modular.webserver.handler.registry :refer [handler-registry]]
   [modular.ws.core :refer [init-ws!]]))

(defn jetty-ws-map []
  (let [jetty-ws (get-in-config [:webly/web-server :jetty-ws])
        v  (map (fn [[route kw]]
                  [route (get @handler-registry kw)])
                jetty-ws)]
    (debug "jetty ws map:" jetty-ws)
    (into {} v)))

(defn jetty-ws-handler []
  (let [conn (init-ws! :jetty)
        ws-map (jetty-ws-map)]
    ws-map))

(defn start [ring-handler profile]
  (let [{:keys [type api wrap-handler-reload]} (get-in profile [:server])
        jetty-config (get-in-config [:webly/web-server])
        api true ; always use non blocking mode
        jetty-config (if api
                       (do  (debug "using web-server-api")
                            (assoc jetty-config :join? false))
                       (assoc jetty-config :join? true))]
    (warn "jetty config: " jetty-config)
    (case type
      :jetty (run-jetty-server ring-handler
                               (jetty-ws-handler)
                               jetty-config)
      ;:undertow (run-undertow-server ring-handler port host api)
      ;:httpkit (run-httpkit-server ring-handler port host api)
      ;:shadow (run-shadow-server)
      (error "start-server failed: server type not found: " type))))

(defn stop [jetty-instance]
  (info "stopping jetty..")
  ;(info jetty-instance)
  (.stop jetty-instance))