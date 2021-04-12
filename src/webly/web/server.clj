(ns webly.web.server
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [webly.config :refer [load-config! get-in-config config-atom]]
   [webly.web.middleware :refer [wrap-webly]]
   ; ws
   [webly.ws.core :refer [init-ws!]]
   [webly.ws.handler :refer [ws-handshake-handler]]
   ; jetty
   [ring.adapter.jetty9 :refer [run-jetty]]
   ;undertow
   [ring.adapter.undertow :refer [run-undertow]]
   ;
   [shadow.cljs.devtools.server :as shadow-server]))

(defn run-jetty-server [ring-handler]
  (let [conn (init-ws! :jetty)
        {:keys [port host]} (get-in-config [:web-server])]
    (info "Starting Jetty web server at port " port " ..")
    (run-jetty ring-handler {:port port
                             :websockets  {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                             :allow-null-path-info true
                           ;:join?  false        
                             })))


; https://github.com/luminus-framework/ring-undertow-adapter


(defn run-undertow-server [ring-handler port host]
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
    (shadow-server/start!)
    ;(shadow-server/stop!)
    ))

(defn run-server [ring-handler profile]
  (let [server-type (get-in profile [:server :type])
        {:keys [port host]} (get-in-config [:web-server])]
    (case server-type
      :jetty (run-jetty-server ring-handler)
      :undertow (run-undertow-server ring-handler port host)
      :shadow (run-shadow-server)
      (error "run-server failed: server type not found: "))))
