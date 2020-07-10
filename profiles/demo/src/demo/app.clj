(ns demo.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server :as shadow-server]   
   [webly.web.handler :refer [make-handler]]
   [demo.routes :refer [demo-routes-backend]]
   [demo.handler] ; side-effects
   ))

;(timbre/set-level! :trace) ; Uncomment for more logging
;  (timbre/set-level! :debug)
(timbre/set-level! :info)

(def handler (make-handler demo-routes-backend))

(defn -main
  {:shadow/requires-server true}
  [& args]
  (info "Starting with args: " args)
  (shadow-server/start!)
  (shadow/watch :demo {:verbose true}))
