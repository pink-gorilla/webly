(ns demo.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch-sync]]
   [pinkgorilla.ui.config :refer [set-prefix!]]
   [webly.web.views :refer [webly-app]]
   [webly.config] ; side-effects
   [demo.routes :refer [demo-routes-backend]]))

(set-prefix! "/r/")

(defn print-log-init! []
  (enable-console-print!)
;(timbre/set-level! :trace) ; Uncomment for more logging
  (timbre/set-level! :debug)
  #_(timbre/set-level! :info))

(defn mount-app []
  (dispatch-sync [:bidi/init demo-routes-backend])
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load before-reload []
  (println "shadow-cljs reload: before")
  (info "shadow-cljs reload: before"))

(defn ^:dev/after-load after-reload []
  (print-log-init!)
  (println "shadow-cljs reload: after")
  (info "shadow-cljs reload: after")

  (println "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  ;(println "re-loading configuration from server..")
  ;(dispatch [:load-config])

  ;(init-routes)
  ;(start-router!)
  (println "mounting webly-app ..")
  (mount-app))

(after-reload)

(defn ^:export start []
  (info "webly demo starting ..")
  (mount-app))

