(ns demo.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server :as shadow-server]
   [webly.web.handler :refer [make-handler]]
   [webly.config :refer [webly-config]]
   [demo.routes :refer [demo-routes-backend demo-routes-frontend]]
   [demo.handler] ; side-effects
   ))

(swap! webly-config assoc :start "demo.app.start (); ")

(timbre/set-level! (:timbre-loglevel @webly-config))

(info "making handler ..")
(def handler (make-handler demo-routes-backend demo-routes-frontend))

(defn -main
  {:shadow/requires-server true}
  [& args]
  (info "Starting with args: " args)
  (shadow-server/start!)
  (shadow/watch :demo {:verbose true}))
