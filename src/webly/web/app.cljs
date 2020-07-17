(ns webly.web.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch dispatch-sync]]
   [webly.web.views :refer [webly-app]]
   [webly.user.dialog] ; side-effects
   [webly.config :refer [webly-config]] ; side-effects
   ))

(defn print-log-init! []
  (enable-console-print!)
  (timbre/set-level! (:timbre-loglevel @webly-config)))

(defn mount-app []
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

(defn start [routes]
  (dispatch [:bidi/init routes]))

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load
  before-load []
  (println "shadow-cljs reload: before")
  (info "shadow-cljs reload: before"))

(defn ^:dev/after-load
  after-load []
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

(after-load)


