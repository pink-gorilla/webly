(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config config-atom load-config! resolve-config-key]]
   [modular.require :refer [require-namespaces]]
   [modular.system :as system]
   [modular.writer :refer [write-status]]
   [modular.webserver.middleware.dev :refer [wrap-dev]]
   [modular.oauth2.handler] ; side-effects
   [modular.oauth2.middleware :refer [print-oauth2-config]]
   [modular.ws.msg-handler :refer [permission-fn-a]]
   [modular.oauth2.local.handler :refer [service-authorized?]]
   [modular.oauth2] ; side-effects
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.web.server :as web-server]
   [webly.web.handler :refer [make-handler]]
   [webly.app.handler :refer [app-handler]]
   [webly.app.routes :refer [make-routes-frontend make-routes-backend]])
  (:gen-class))

(defn start-permissions []
  (if (and (get-in-config [:permission])
           (get-in-config [:users]))
    (do (info "starting webly ws services with PERMISSIONS enabled.")
        (reset! permission-fn-a service-authorized?))
    (warn "webly ws services are NOT PERMISSION enabled.")))

(defn create-ring-handler
  "creates a ring-handler
   uses configuration in webly-config to do so
   the def statement defines a variable in this ns. This is used by shadow-cljs to resolve the handler.
   "
  []
  (debug "webly creating ring handler.. ")
  (let [routes (get-in-config [:webly :routes])
        routes-backend (make-routes-backend (:app routes) (:api routes))
        routes-frontend (make-routes-frontend (:app routes))
        ;_ (info "all-api-routes:" routes-backend "all-app-routes:" routes-frontend)
        wrap-handler-reload (get-in-config [:wrap-handler-reload])
        h (make-handler app-handler routes-backend routes-frontend)
        h (if wrap-handler-reload (wrap-dev h) h)]

    (write-status "routes" {:frontend routes-frontend :backend routes-backend})
    (print-oauth2-config)
    (def ring-handler h)))

(defn run-server-p [profile]
  (info "webly starting webserver : " (:server profile))
  (create-ring-handler)
  (start-permissions)
  (web-server/start ring-handler profile))

(defn build-p [profile]
  (debug "webly starting build:  " (:bundle profile))
  (build profile))

(defn run-profile [profile-name]
  (let [profile (setup-profile profile-name)]
    (if (:server profile)
      (run-server-p profile)
      (info "not running web server"))
    (if (:bundle profile)
      (build-p profile)
      (info "not building bundle."))
    (:server profile) ; return value
    ))

(defn start-webly [config profile-name]
  (resolve-config-key config [:webly :routes])
  (let [profile (setup-profile profile-name)
        webserver  (when (:server profile)
                     (run-server-p profile))
        shadow (when (:bundle profile)
                 (build-p profile))]
    {:profile profile
     :webserver webserver
     :shadow shadow}))

(defn stop-webly [{:keys [profile webserver shadow]}]
  (info "stopping webly..")
  (when webserver
    (web-server/stop webserver))
  (when shadow
    (stop-shadow shadow)))

(defn webly-build [{:keys [config profile]}]
  (load-config! config)
  (require-namespaces (get-in-config [:ns-clj]))
  (resolve-config-key config [:webly :routes])
  (let [profile (setup-profile profile)]
    (when (:bundle profile)
      (build-p profile))))
