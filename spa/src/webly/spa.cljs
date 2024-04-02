(ns webly.spa
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   ; side-effects
   [ajax.core :as ajax] ; https://github.com/JulianBirch/cljs-ajax used by http-fx
   [day8.re-frame.http-fx]
   ; frontend

   [frontend.dialog]
   [frontend.page]
   [webly.spa.service.keybindings :refer [start-keybindings]]
   [webly.spa.service.theme :refer [start-theme]]
   [webly.spa.service.ga :refer [start-ga]]
   [webly.spa.service.bidi :refer [start-bidi]]
   [webly.spa.service.ws :refer [start-ws]]
   [webly.spa.service.config :refer [start-config]]
   [webly.spa.service.timbre :refer [timbre-config!]]
   ; webly
   [webly.build.lazy]
   [webly.module.build :refer [add-lazy-modules print-build-summary webly-resolve]]
   [webly.spa.tenx.events]
   [webly.spa.views :refer [webly-app]]
   [webly.spa.events] ; side effects
   [webly.spa.loader.page] ; side-effects
   [webly.build.prefs :refer [pref]]
   [webly.spa.mode :refer [set-mode! mode-a get-resource-path]]))

(add-lazy-modules)

(warn "setting frontend.page resolver to webly-resolve..")
(frontend.page/set-resolver! webly-resolve)

;; see:
;; https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks
;; https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html

;; configuration is done EITHER
;; - EITHER via METADATA
;; - OR shadow-cljs.edn :devtools section   

;; before-reload is a good place to stop application stuff before we reload.

(defn mount-app []
  (reagent.dom/render [webly-app]
                      (.getElementById js/document "app")))

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
  (mount-app))

(defn remove-spinner []
  (let [spinner (.. js/document (getElementById "spinner"))
        body-classes (.. js/document -body -classList)]
    ;(println "spinner: " spinner)
    ;(println "cl: " body-classes)
    (.. spinner (remove))
    (when (.contains body-classes "loading")
      (.remove body-classes "loading"))))

(reg-event-db
 :webly/app-after-config-load
 (fn [db [_ static?]]
   (let [spa (get-in db [:config :spa])
         start-user-app (-> spa :start-user-app)
         frontend-routes (get-in db [:config :frontend-routes])
         theme (get-in db [:config :theme])
         keybindings (get-in db [:config :keybindings])
         timbre-cljs (get-in db [:config :timbre/cljs])]
     (info "webly config after-load")
     (remove-spinner)
     (dispatch [:webly/status :configuring-app])
     (print-build-summary)
     ; services
     (timbre-config! timbre-cljs)
     (start-bidi frontend-routes)
     (start-ga)
     (start-keybindings keybindings)
     (start-theme theme)
     (if static?
       (warn "websockets are deactivated in static mode.")
       (start-ws))
     ; 
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
  (set-mode! mode)
  (dispatch [:reframe10x-init])
  (dispatch [:webly/status :route-init])
  (dispatch [:webly/status :loading-config])
  (start-config)
  (mount-app))
