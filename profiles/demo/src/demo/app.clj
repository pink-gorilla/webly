(ns demo.app
  (:require
   [webly.user.app.app :refer [webly-run!]]
   [webly.profile :refer [server?]]
   ; side-effects
   [demo.routes]
   [demo.handler]
   [demo.time :refer [start-time-sender!]]))

(defn -main
  "app exists, so 
      1. side-effects are loaded.
      2. server-side services can be started via the lein cli interface"
  [profile-name]
  (when (server? profile-name)
    (start-time-sender!))
  (webly-run! profile-name))

