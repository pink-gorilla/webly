(ns webly.user.app.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer [info]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]
   [webly.user.app.views :refer [webly-app]]

   ; side-effects
   [webly.web.events-bidi]
   [webly.user.config.events]
   [webly.user.config.subscription]
   [webly.user.dialog] ; side-effects
   [webly.user.markdown.subscriptions]
   [webly.user.markdown.events]
   [webly.user.markdown.page] ; reagent-page: md 
   [webly.user.analytics.events]
   [webly.user.oauth2.page] ; reagent-page: oauth2 redirect
   [webly.user.oauth2.events]
   [webly.user.oauth2.subscriptions]))

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
  (webly.user.app.app/mount-app))

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

(defn webly-run! [user-routes-api user-routes-app]
  (let [routes (make-routes-frontend user-routes-app)] ; user-routes-api
    (info "webly-run!  ...")
    (start routes)
    (mount-app)))
