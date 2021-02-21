(ns demo.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [webly.build :refer [build-cli]]
   [webly.web.handler :refer [make-handler]]
   [webly.config :refer [webly-config]]
   [webly.oauth2.default-config] ; side-effects
   [demo.routes :refer [demo-routes-backend demo-routes-frontend]]
   [demo.handler] ; side-effects
   ))

(defn -main
  [mode]
  (let [mode (or mode "watch")]
    (info "demo starting mode: " mode)
    (swap! webly-config assoc 
           :timbre-loglevel :debug
           :title "Webly Demo"
           :start "demo.app.start (); ")
    (def handler (make-handler demo-routes-backend demo-routes-frontend))
    (build-cli mode "+demo" "demo.app/handler" "demo.app")))

