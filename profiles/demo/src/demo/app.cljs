(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch-sync]]
   [webly.config :refer [webly-config]]
   [webly.web.app]
   [demo.routes :refer [demo-routes-backend]]
   [demo.views] ; side-effects
   ))

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load before-reload []
  (println "shadow-cljs reload: before")
  (info "shadow-cljs reload: before"))

(defn ^:dev/after-load after-reload []
  (webly.web.app/print-log-init!)
  (println "shadow-cljs reload: after")
  (info "shadow-cljs reload: after")

  (println "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  ;(println "re-loading configuration from server..")
  ;(dispatch [:load-config])

  ;(init-routes)
  ;(start-router!)
  (println "mounting webly-app ..")
  (webly.web.app/mount-app))

(after-reload)

(defn ^:export start []
  (info "webly demo starting ..")
  (webly.web.app/start demo-routes-backend)
  (webly.web.app/mount-app))

