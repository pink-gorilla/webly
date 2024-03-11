(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [load-config! config-atom]]
   [modular.writer :refer [write-status]]
   [modular.permission.service :refer [service-authorized?]]
   [modular.permission.app :refer [start-permissions]]
   [modular.ws.core :refer [start-websocket-server]]
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.web.server :as web-server]
   [webly.spa.handler.core :as webly-handler]
   [webly.spa.handler.routes.config :refer [create-config-routes]]
   )
  (:gen-class))


(defn watch? [profile-name]
  (case profile-name
    "watch" true
    "watch2" true
    false))

;; HANDLER RELATED

(defn hack-routes-symbol [routes]
  (if (symbol? routes)
      (-> routes requiring-resolve var-get)
      routes))
    

(defn create-ring-handler [config routes config-route websocket-routes]
  (let [routes (hack-routes-symbol routes)
        {:keys [handler routes]} (webly-handler/create-ring-handler config routes config-route websocket-routes)]
    (def ring-handler handler) ; needed by shadow-watch
    (write-status "routes" routes)
    handler))

(defn ensure-keyword [kw]
  (if (keyword? kw)
    kw
    (keyword kw)))

(defn start-webly [{:keys [sente-debug?]
                    :or {sente-debug false}
                    :as config} server-type]
  (info "start-webly: " server-type)
  (let [server-type (ensure-keyword server-type)
        permission (start-permissions)
        routes (hack-routes-symbol (get-in config [:webly :routes]))
        frontend-routes (:app routes)
        _ (warn "routes frontend: " frontend-routes)
        config-route (create-config-routes config frontend-routes)
        sente-debug? false
        websocket (start-websocket-server server-type sente-debug?)
        websocket-routes (:bidi-routes websocket)
        ring-handler (create-ring-handler config routes config-route websocket-routes)
        webserver  (let [webserver-config (get-in config [:webly/web-server])]
                     (if (watch? server-type)
                       (web-server/start webserver-config ring-handler websocket :jetty)
                       (web-server/start webserver-config ring-handler websocket (keyword server-type))))
        shadow   (when (watch? server-type)
                   (let [profile-full (setup-profile server-type)]
                     (when (:bundle profile-full)
                       (build profile-full))))]
    ; return config of started services (needed to stop)
    {:profile server-type
     :permission permission
     :websocket websocket
     :webserver webserver
     :shadow shadow}))

(defn stop-webly [{:keys [profile webserver shadow]}]
  (info "stopping webly..")
  (when webserver
    (web-server/stop webserver))
  (when shadow
    (stop-shadow shadow)))

;; BUILD

(defn webly-build [{:keys [config profile]}]
  (load-config! config)
  ;(require-namespaces (get-in-config [:ns-clj])) ; 2023 07 awb: not needed for build
  ;(resolve-config-key config [:webly :routes]); 2023 07 awb: not needed for build
  (let [profile (setup-profile profile)]
    (when (:bundle profile)
      (build profile))))

