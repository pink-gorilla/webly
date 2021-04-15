(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [webly.config :refer [load-config! get-in-config config-atom]]
   [webly.web.middleware :refer [wrap-webly]]
   [webly.web.handler :refer [handler-registry]]
   ; ws
   [webly.ws.core :refer [init-ws!]]
   [webly.ws.handler :refer [ws-handshake-handler]]
   ; jetty
   [ring.adapter.jetty9 :refer [run-jetty]]
   ;undertow
   [ring.adapter.undertow :refer [run-undertow]]
   ;
   ;httpkit
   [org.httpkit.server :as httpkit]

   [shadow.cljs.devtools.server :as shadow-server]))

(defn jetty-ws-map []
  (let [jetty-ws (get-in-config [:jetty-ws])
        v  (map (fn [[route kw]]
                  [route (get @handler-registry kw)])
                jetty-ws)]
    (info "jetty ws map:" jetty-ws)
    (into {} v)))

(defn run-jetty-server [ring-handler port host api]
  ; https://github.com/sunng87/ring-jetty9-adapter  
  (let [conn (init-ws! :jetty)
        ws-map (jetty-ws-map)]
    (info "Starting Jetty web server at port " port)
    (run-jetty ring-handler {:port port
                             :host host
                             :websockets ws-map ; {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                             :allow-null-path-info true ; omit the trailing slash from your URLs
                             :ws-max-idle-time 3600000 ; important for nrepl middleware 
                             :join?  (if api false true)})))

(defn run-httpkit-server
  [ring-handler port host api]
  (let [conn (init-ws! :httpkit)]
    (info "starting httpkit web at " port host)
    (httpkit/run-server ring-handler {:port port
                                      :host host})))

; https://github.com/luminus-framework/ring-undertow-adapter
(defn run-undertow-server [ring-handler port host api]
  (require '[ring.adapter.undertow :refer [run-undertow]])
  (let [;run-undertow (resolve)
        conn (init-ws! :undertow)]
    (info "Starting Undertow web server at port " port " ..")
    (run-undertow ring-handler {:port port
                                :host host
                            ; :websockets  {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                             ;:allow-null-path-info true
                           ;:join?  false        
                                })))

(defn run-shadow-server []
  (let [conn (init-ws! :undertow)]
    ;(shadow-server/start!)
    ;(shadow-server/stop!)
    ))

(defn run-server [ring-handler profile]
  (let [{:keys [type api wrap-handler-reload]} (get-in profile [:server])
        web-server (if api :web-server-api :web-server)
        {:keys [port host]} (get-in-config [web-server])]
    (if api
      (info "using web-server-api"))
    (case type
      :jetty (run-jetty-server ring-handler port host api)
      :undertow (run-undertow-server ring-handler port host api)
      :httpkit (run-httpkit-server ring-handler port host api)
      ;:shadow (run-shadow-server)
      (error "run-server failed: server type not found: "))))
