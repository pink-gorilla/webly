(ns modular.webserver.jetty
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [ring.adapter.jetty9 :refer [run-jetty]]))

(defn run-jetty-server [ring-handler ws-map user-opts]
  (let [default-opts {:allow-null-path-info true ; omit the trailing slash from your URLs
                      :ws-max-idle-time 3600000 ; important for nrepl middleware 
                      }
        ws-opts (if ws-map
                  {:websockets ws-map}  ; {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                  {})]
  ; https://github.com/sunng87/ring-jetty9-adapter  
    (info "Starting Jetty web server .. ")
    (run-jetty ring-handler (merge default-opts ws-opts user-opts))))

