(ns webly.app.app
  (:require
   [reagent.dom]
   ;[cljs.pprint]
   [taoensso.timbre :refer-macros [info warn]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   ; side-effects
   [ajax.core :as ajax] ; https://github.com/JulianBirch/cljs-ajax used by http-fx
   [day8.re-frame.http-fx]
   [modular.oauth2]
   [modular.ws.events]
   [modular.ws.core]
   ; frontend
   [frontend.config.events]
   [frontend.config.subscription]
   [frontend.routes.events]
   [frontend.css.events]
   [frontend.css.loading]
   [frontend.css.subscriptions]
   [frontend.keybindings.events]
   [frontend.analytics.events]
   [frontend.settings.events]
   [frontend.settings.subscriptions]
   [frontend.dialog]
   [frontend.routes :refer [set-main-path!]]
   ; webly
   [webly.build.lazy]
   [webly.app.tenx.events]
   [webly.app.views :refer [webly-app]]
   [webly.app.events]
   [webly.app.routes :refer [make-routes-frontend make-routes-backend]]
   [webly.app.status.page] ; side-effects

   [webly.build.prefs :refer [pref]]
;   [webly.app.static :refer [make-static-adjustment]]
;   [frontend.config.core :refer [webly-mode-atom]]
   ))

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
  (webly.app.app/mount-app))

(defn remove-spinner []
  (let [spinner (.. js/document (getElementById "spinner"))
        body-classes (.. js/document -body -classList)]
    ;(println "spinner: " spinner)
    ;(println "cl: " body-classes)
    (.. spinner (remove))
    (when (.contains body-classes "loading")
      (.remove body-classes "loading"))))

(defn setup-bidi [user-routes-api user-routes-app]
  (let [routes-frontend (make-routes-frontend user-routes-app)
        routes-backend (make-routes-backend user-routes-app user-routes-api)]
    (dispatch [:bidi/init routes-frontend routes-backend])))

(reg-event-db
 :webly/app-after-config-load
 (fn [db [_ static?]]
   (let [routes (get-in db [:config :webly :routes])
         start-user-app (get-in db [:config :webly :start-user-app])]
     (info "webly config after-load")
     (remove-spinner)
     (dispatch [:webly/status :configuring-app])
     (setup-bidi (:api routes) (:app routes))
     (dispatch [:ga/init])
     (dispatch [:keybindings/init])
     (dispatch [:css/init])
     (dispatch [:settings/init])
     (if static?
       (warn "websockets are deactivated in static mode.")
       (dispatch [:ws/init]))
     (dispatch [:webly/set-status :configured? true])

     (if start-user-app
       (do (info "starting user app: " start-user-app)
           (dispatch start-user-app))
       (warn "no user app startup defined."))
     ;(dispatch [:webly/status :running])
     )
   db))

(defn ^:export start [mode]
  (enable-console-print!)
  (println "webly starting mode:" mode)
  (info "webly starting mode: " mode " prefs: " (pref))
  (let [static? (= mode "static")
        main-path (:main-path (pref))
        asset-path (:asset-path (pref))]
    (when static?
      (set-main-path! main-path))
    (dispatch [:reframe10x-init])
    (dispatch [:webly/status :route-init])
    (dispatch [:webly/status :loading-config])
    (dispatch [:config/load :webly/app-after-config-load static? main-path])
    (mount-app)))
