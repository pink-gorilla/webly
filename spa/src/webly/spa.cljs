(ns webly.spa
  (:require
   [reagent.dom]
   [taoensso.timbre :refer-macros [info warn error]]
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
   [webly.spa.service.ws :refer [start-ws]]
   ; webly
   [webly.build.lazy]
   [webly.spa.views :refer [webly-app]]
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
  (clear-subscription-cache!))

(defn remove-spinner []
  (let [spinner (.. js/document (getElementById "spinner"))
        body-classes (.. js/document -body -classList)]
    ;(println "spinner: " spinner)
    ;(println "cl: " body-classes)
    (.. spinner (remove))
    (when (.contains body-classes "loading")
      (.remove body-classes "loading"))))

(def ws-open-p (p/deferred))

(reg-event-db
 :ws/open-first
 (fn [db [_ state-map]]
   (info "ws connected for the first time!")
   (p/resolve! ws-open-p true)
   db))

(reg-event-db
 :webly/app-after-config-load
 (fn [db [_ static?]]
   (let [cljs-services (get-in db [:config :cljs-services])
         services-p  (start-cljs-services cljs-services)
         http-ports (get-in db [:config :ports])
         all-p (if static?
                 (do (warn "websockets are deactivated in static mode.")
                     services-p)
                 (do (start-ws http-ports)
                     (p/all [services-p ws-open-p])))]
     (info "webly config after-load")
     (remove-spinner)
     (info "mounting webly-app ..")
     (mount-app) ; mount needs to wait until config is loaded.
     (dispatch [:webly/status :configuring-app])

     (-> all-p
         (p/then (fn [_]
                   (warn "services/websocket running!")
                   (dispatch [:webly/set-status :configured? true])
                   (dispatch [:webly/status :running])))
         (p/catch (fn [err]
                    (error "service start error: " err)))))

   db))

(defn ^:export start [_mode]
  (enable-console-print!)
  (dispatch [:webly/status :route-init])
  (dispatch [:webly/status :loading-config])
  (start-config))
