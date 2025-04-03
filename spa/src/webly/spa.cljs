(ns webly.spa
  (:require
   [taoensso.timbre :refer-macros [info warn error]]
   [promesa.core :as p]
   ; frontend
   [webly.spa.service.config :refer [get-config]]
   [webly.spa.service :refer [start-cljs-services]]
   [webly.spa.resolve :refer [get-resolver]]
   ; webly
   [shadowx.build.lazy]))

;; see:
;; https://shadow-cljs.github.io/docs/UsersGuide.html#_lifecycle_hooks
;; https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html

;; configuration is done EITHER
;; - EITHER via METADATA
;; - OR shadow-cljs.edn :devtools section   

;; before-reload is a good place to stop application stuff before we reload.

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
  (let [{:keys [ports static? cljs-services spa]} config
        services-p  (start-cljs-services cljs-services)]
    (-> services-p
        (p/then (fn [_]
                  (warn "webly bootstrap done. mounting app")
                  (remove-spinner)
                  (info "mounting webly-app ..")
                  ; mount needs to wait until config is loaded.
                  (let [resolve-fn (get-resolver)
                        mount-fn  (:mount-fn spa)
                        _ (println "mounting: " mount-fn)
                        mount-p (resolve-fn mount-fn)
                        _ (println "mount-p: " mount-p)]
                    (-> mount-p
                        (p/then (fn [mount]
                                  (println "mount-fn resolved to: " mount)
                                  (mount)))
                        (p/catch (fn [err]
                                   (println "mount-fn error: " err)))))))
        (p/catch (fn [err]
                   (error "service start error: " err))))))

(defn ^:export start [_mode]
  (enable-console-print!)
  (let [config-p (get-config)]
    (p/then config-p start-app)))
