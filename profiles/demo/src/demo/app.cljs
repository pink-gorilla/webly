(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.web.app]
   [webly.config :refer [webly-config]]
   [webly.oauth2.default-config] ; side-effects
   [demo.routes :refer [demo-routes-backend]]
   [demo.views] ; side-effects   
   ))

(defn ^:export start []
  (swap! webly-config assoc :timbre-loglevel :info)
  (info "webly demo starting ..")
  (webly.web.app/start demo-routes-backend)
  (webly.web.app/mount-app))

