(ns webly.web.app
  (:require
   [reagent.dom]
   [taoensso.timbre :as timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch-sync]]
   [pinkgorilla.ui.config :refer [set-prefix!]]
   [webly.web.views :refer [webly-app]]
   [webly.config :refer [webly-config]] ; side-effects
   ))

(set-prefix! "/r/")

(defn print-log-init! []
  (enable-console-print!)
  (timbre/set-level! (:timbre-loglevel @webly-config)))

(defn mount-app []
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

(defn start [routes]
  (dispatch-sync [:bidi/init routes]))



