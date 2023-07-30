(ns modular.webserver.httpkit
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [org.httpkit.server :as httpkit]))

; http://http-kit.github.io/
;https://github.com/http-kit/http-kit
; this dependency is not part of webly dependencies:  ;{http-kit "2.5.3"}

(defn run-server [ring-handler config]
  (let [default-opts {:allow-null-path-info true ; omit the trailing slash from your URLs
                      :ws-max-idle-time 3600000 ; important for nrepl middleware 
                      }
        {:keys [port]} config]
    (info "Starting Httpkity web server (http:" port ")")
    (httpkit/run-server ring-handler {:port port
                                      :host "0.0.0.0"})))

