(ns webly.spa
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [promesa.core :as p]
   ; side-effects
   [ajax.core :as ajax] ; https://github.com/JulianBirch/cljs-ajax used by http-fx
   [day8.re-frame.http-fx]
   ; frontend
   [frontend.dialog]
   [frontend.page]
   [webly.spa.service.config :refer [start-config]]
   [webly.spa.service :refer [start-cljs-services]]
   [webly.spa.service.ga :refer [start-ga]]
   [webly.spa.service.ws :refer [start-ws]]
   ; webly
   [webly.build.lazy]
   [webly.spa.tenx.events]
   [webly.spa.views :refer [webly-app]]
   [webly.spa.events] ; side effects
   [webly.spa.loader.page] ; side-effects
   ))

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
         cljs-services (get-in db [:config :cljs-services])
         services-p  (start-cljs-services cljs-services)]
     (info "webly config after-load")
     (remove-spinner)
     (dispatch [:webly/status :configuring-app])
     ; services
     (start-ga)
     (if static?
       (warn "websockets are deactivated in static mode.")
       (start-ws))
     ; 
     (p/then services-p (fn [_]
                          (warn "services are all configured!")
                          (dispatch [:webly/set-status :configured? true])
                          (if start-user-app
                            (do (info "starting user app: " start-user-app)
                                (dispatch start-user-app))
                            (warn "no user app startup defined."))
                            ;(dispatch [:webly/status :running])                     
                          )))
   db))

(defn ^:export start [mode]
  (enable-console-print!)
  (dispatch [:reframe10x-init])
  (dispatch [:webly/status :route-init])
  (dispatch [:webly/status :loading-config])
  (start-config)
  (mount-app))
