(ns modular.webserver.jetty
  (:require
   [taoensso.timbre :as timbre :refer [info warn error]]
   [ring.adapter.jetty9 :refer [run-jetty]]
   [modular.config :refer [get-in-config]]))

(defn run-jetty-server [ring-handler ws-map port host api]
  (let [opts {:port port
              :host host
              :allow-null-path-info true ; omit the trailing slash from your URLs
              :ws-max-idle-time 3600000 ; important for nrepl middleware 
              :join?  (if api false true)}
        opts (if ws-map
               (assoc opts :websockets ws-map)  ; {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
               opts)]
  ; https://github.com/sunng87/ring-jetty9-adapter  
    (info "Starting Jetty web server at port " port)
    (run-jetty ring-handler opts)))