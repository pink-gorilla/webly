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
  []
  (info "demo starting..")
  (swap! webly-config assoc :timbre-loglevel :info)
  (swap! webly-config assoc :title "Webly Demo")
  (swap! webly-config assoc :start "demo.app.start (); ")

  (info "making handler ..")
  (def handler (make-handler demo-routes-backend demo-routes-frontend))

  (info "demo watching..")
  (build-cli :watch "+dev" "demo.app/handler" "demo.app"))
