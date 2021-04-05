(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.user.app.app :refer [webly-run!]]
   [demo.routes :refer [demo-routes-api demo-routes-app]]
   [demo.views] ; side-effects   
   ))

(defn ^:export start []
  (info "webly demo starting ...")
  (webly-run! demo-routes-api demo-routes-app))

