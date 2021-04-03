(ns webly.web.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db]]

    ; side-effects
   [webly.web.views :refer [webly-app]]
   [webly.user.dialog] ; side-effects
   [webly.web.events-bidi]
   [webly.user.config.events]
   [webly.user.markdown.subscriptions]
   [webly.user.markdown.events]
   [webly.user.markdown.view] ; bidi route registration
   [webly.user.analytics.events]
   [webly.user.config.subscription]))

(defn mount-app []
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

;; before-reload is a good place to stop application stuff before we reload.
(defn ^:dev/before-load
  before-load []
  (info "before-load"))

(defn ^:dev/after-load
  after-load []
  (enable-console-print!)
  (info "after-load")

  (info "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  (info "mounting webly-app ..")
  (dispatch [:ga/event {:category "webly" :action "mounted" :label 77 :value 13}])
  (webly.web.app/mount-app))

;(after-load)

(reg-event-db
 :webly/app-after-config-load
 (fn [db [_]]
   (info "webly config after-load")
   (dispatch [:ga/init])
   (dispatch [:markdown/init])
   (dispatch [:markdown/load-index])

   db))

(defn start [routes]
  (dispatch [:config/load :webly/app-after-config-load])
  (dispatch [:bidi/init routes]))


