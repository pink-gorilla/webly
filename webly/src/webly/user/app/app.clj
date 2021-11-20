(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.writer :refer [write-status]]
   [modular.config :refer [get-in-config config-atom load-config!]]
   [modular.webserver.middleware.dev :refer [wrap-dev]]
   [webly.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.web.server :refer [run-server]]
   [webly.web.handler :refer [make-handler]]
   [webly.user.app.handler :refer [app-handler]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]
   [webly.user.oauth2.middleware :refer [print-oauth2-config]]
   ; side-effects
   [webly.user.config.handler]   ; handler: config
   [webly.user.oauth2.handler]
   [webly.user.app.keybindings])
  (:gen-class))

(defn start-services [profile]
  (let [start-service (get-in-config [:webly :start-service])]
    (if start-service
      (do (info "starting services : " (:server profile))
          (try
            (info "start-service:" start-service)
            (if-let [f (resolve start-service)]
              (f)
              (error "services symbol [" start-service "] could not get resolved!"))
            (catch Exception e
              (error "Exception starting services: " (pr-str e)))))
      (warn "no services defined."))))

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
  (start-services profile)
  (info "webly starting webserver : " (:server profile))
  (create-ring-handler)
  (run-server ring-handler profile))

(defn build-p [profile]
  (debug "webly starting build:  " (:bundle profile))
  (build profile))

(defn webly-run!
  [profile-name config]
  (when (empty? @config-atom)
    (warn "config is empty.. loading config now!")
    (load-config! config))
  (let [profile (setup-profile profile-name)]
    (if (:server profile)
      (run-server-p profile)
      (info "not running web server"))
    (if (:bundle profile)
      (build-p profile)
      (info "not building bundle."))))

(defn webly-run [{:keys [profile config]}]
  (webly-run! profile config))

(defn -main ; for lein alias
  ([]
   (webly-run {}))
  ([profile]
   (webly-run {:profile profile}))
  ([config profile]   ; when config and profile are passed, config first (because profile then can get changed in cli)
   (webly-run {:profile profile
               :config config})))