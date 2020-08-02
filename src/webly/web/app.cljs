(ns webly.web.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch]]
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

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load
  before-load []
  (info "before-load"))

(defn ^:dev/after-load
  after-load []
  (webly.web.app/print-log-init!)
  (info "after-load")

  (info "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  (info "mounting webly-app ..")
  (webly.web.app/mount-app))

;(after-load)

(defn start [routes]
  (dispatch [:bidi/init routes]))


