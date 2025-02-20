(ns webly.spa
  (:require
   [reagent.dom.client :as rdom]
   [taoensso.timbre :refer-macros [info warn error]]
   [re-frame.core :refer [clear-subscription-cache! dispatch reg-event-db reg-sub]]
   [promesa.core :as p]
   ; frontend
   ;[frontend.dialog]
   [webly.spa.service.config :refer [get-config]]
   [webly.spa.service :refer [start-cljs-services]]
   ; webly
   [shadowx.build.lazy]
   [webly.spa.views :refer [webly-app]]))

;; see:
;; https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks
;; https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html

;; configuration is done EITHER
;; - EITHER via METADATA
;; - OR shadow-cljs.edn :devtools section   

;; before-reload is a good place to stop application stuff before we reload.

(defn mount-app []
  (let [root (rdom/create-root (.getElementById js/document "app"))]
    (rdom/render root [webly-app])))

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

(defn start-app [config]
  (info "webly config after-load")
  (let [{:keys [ports static? cljs-services]} config
        services-p  (start-cljs-services cljs-services)
        ;all-p (if static?
        ;         (do (warn "websockets are deactivated in static mode.")
        ;             services-p)
        ;         (let [ws-p (start-ws-p ports)]
        ;           (p/all [services-p ws-p])))
        ]
    (-> services-p
        (p/then (fn [_]
                  (warn "webly bootstrap done. mounting app")
                  (remove-spinner)
                  (info "mounting webly-app ..")
                  (mount-app) ; mount needs to wait until config is loaded.
                  ))
        (p/catch (fn [err]
                   (error "service start error: " err))))))

(defn ^:export start [_mode]
  (enable-console-print!)
  (let [config-p (get-config)]
    (p/then config-p start-app)))
