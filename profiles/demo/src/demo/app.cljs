(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [webly.user.app.app :refer [webly-run!]]
   [demo.routes :refer [routes-api routes-app]]
   ; side-effects 
   [demo.events]
   [demo.time]
   [demo.pages.main]
   [demo.pages.help]
   [demo.pages.party]))

(defn ^:export start []
  (info "webly demo starting ...")
  (webly-run! routes-api routes-app))

