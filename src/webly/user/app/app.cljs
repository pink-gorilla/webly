(ns webly.user.app.app
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]
   [webly.user.app.views :refer [webly-app]]

   ; side-effects
   [webly.web.events-bidi]
   [webly.ws.events]
   [webly.user.status.events]
   [webly.user.css.events]
   [webly.user.css.subscriptions]
   [webly.user.config.events]
   [webly.user.config.subscription]
   [webly.user.dialog]
   [webly.user.keybindings.events]
   [webly.user.analytics.events]
   [webly.user.markdown.subscriptions]
   [webly.user.markdown.events]
   [webly.user.markdown.page] ; reagent-page: md 
   [webly.user.oauth2.events]
   [webly.user.oauth2.events-parse]
   [webly.user.oauth2.request-login]
   [webly.user.oauth2.request]
   [webly.user.oauth2.subscriptions]
   [webly.user.tenx.events]
   [webly.user.settings.subscriptions]
   [webly.user.settings.events]
   [webly.user.app.events]
   [webly.user.app.css :as webly-css]))

(defn mount-app []
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

;; see:
;; https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks
;; https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html

;; configuration is done EITHER
;; - EITHER via METADATA
;; - OR shadow-cljs.edn :devtools section   

;; before-reload is a good place to stop application stuff before we reload.


(defn ^:dev/before-load
  before-load []
  (info "before-load")
  (dispatch [:webly/before-load]))

(defn ^:dev/after-load
  after-load []
  (enable-console-print!)
  (info "after-load")

  (info "clearing reframe subscription cache..")
  (clear-subscription-cache!)

  (info "mounting webly-app ..")
  (dispatch [:ga/event {:category "webly" :action "mounted" :label 77 :value 13}])
  (webly.user.app.app/mount-app))

(reg-event-db
 :webly/app-after-config-load
 (fn [db [_]]
   (let [start-user-app (get-in db [:config :webly :start-user-app])]
     (info "webly config after-load")
     (dispatch [:webly/status :configuring-app])
     (dispatch [:ga/init])
     (dispatch [:keybindings/init])
     (dispatch [:css/add-components webly-css/components webly-css/config])
     (dispatch [:settings/init])
     (dispatch [:markdown/init])
     (dispatch [:markdown/load-index])
     (dispatch [:ws/init])
     (if start-user-app
       (do (info "starting user app: " start-user-app)
           (dispatch start-user-app))
       (warn "no user app startup defined."))
     ;(dispatch [:webly/status :running])
     )
   db))

(defn webly-run! [user-routes-api user-routes-app]
  (let [routes-frontend (make-routes-frontend user-routes-app)
        routes-backend (make-routes-backend user-routes-app user-routes-api)]
    (info "webly-run! ...")
    (dispatch [:reframe10x-init])
    (dispatch [:webly/status :route-init])
    (dispatch [:bidi/init routes-frontend routes-backend])
    (dispatch [:webly/status :loading-config])
    (dispatch [:config/load :webly/app-after-config-load])
    (mount-app)))
