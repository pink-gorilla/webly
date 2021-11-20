(ns modular.webserver.jetty
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre :refer [info]]
   [ring.adapter.jetty9 :refer [run-jetty]]))

(defn run-jetty-server [ring-handler ws-map user-opts]
  (let [default-opts {:allow-null-path-info true ; omit the trailing slash from your URLs
                      :ws-max-idle-time 3600000 ; important for nrepl middleware 
                      }
        ws-opts (if ws-map
                  {:websockets ws-map}  ; {"/api/chsk" (wrap-webly (partial ws-handshake-handler conn))}
                  {})
        {:keys [port ssl-port keystore]} user-opts
        https? (and
                ssl-port
                keystore
                (.exists (io/file keystore)))
        user-opts (if https?
                    user-opts
                    (dissoc user-opts :keystore :ssl-port :key-password))]
  ; https://github.com/sunng87/ring-jetty9-adapter  
    (if https?
      (info "Starting Jetty web server (http:" port "https:" ssl-port ")")
      (info "Starting Jetty web server (http:" port "https: none)"))
    (run-jetty ring-handler (merge default-opts ws-opts user-opts))))

