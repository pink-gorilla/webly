(ns demo.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [webly.user.app.app :refer [webly-run!]]

  ; side-effects
   [demo.routes :refer [demo-routes-api demo-routes-app]]
   [demo.handler] ; side-effects   
   ))

(defn -main
  [mode]
  (let [lein-profile "+demo"
        mode (or mode "watch")]
    (info "demo starting mode: " mode)
    (webly-run! mode lein-profile  demo-routes-api demo-routes-app)))

