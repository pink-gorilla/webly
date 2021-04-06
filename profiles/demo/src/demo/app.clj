(ns demo.app
  (:require
   [webly.user.app.app :refer [webly-run!]]
   ; side-effects
   [demo.routes]
   [demo.handler]))

(defn -main
  "app exists, so 
      1. side-effects are loaded.
      2. server-side services can be started via the lein cli interface"
  [mode]
  (let [mode (or mode "watch")]
    (webly-run! mode)))

