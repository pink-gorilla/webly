(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.web.app]
   [webly.config :refer [webly-config]]
   [demo.views] ; side-effects   
   [demo.routes :refer [demo-routes-backend]]))

(defn ^:export start []
  (swap! webly-config assoc :timbre-loglevel :debug)
  (info "webly demo starting ..")
  (webly.web.app/start demo-routes-backend)
  (webly.web.app/mount-app))

