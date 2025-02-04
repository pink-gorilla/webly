(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [extension :refer [discover get-extensions]]
   [modular.config :refer [load-config! get-in-config]]
   [modular.webserver.server :as web-server]
   [modular.ws.core :refer [start-websocket-server]]
   [webly.helper :refer [write-target2]]
   [shadowx.build.profile :refer [setup-profile]]
   [shadowx.build.core :refer [build]]
   [shadowx.build.shadow :refer [stop-shadow]]
   [webly.build.static :refer [build-static]]
   [webly.spa.handler.core :as webly-handler]
   [webly.spa.html.handler :refer [app-handler]]
   [webly.spa.handler.routes.config :refer [create-config-routes]]
   [webly.spa.config :refer [configure]]
   [webly.spa.default :as default]
   [shadowx.default :as shadow-default]
   )
  (:gen-class))

;; shadow watch hack

(defn watch? [profile-name]
  (case profile-name
    "watch" true
    false))

(defn shadow-dev-http-port [config profile]
  (if (watch? profile)
    ;(get-in config [:shadow :dev-http :port])
    (get-in shadow-default/shadow [:dev-http :port])
    0))

;; HANDLER RELATED

(defn create-ring-handler [app-handler routes config-route websocket-routes]
  (let [{:keys [handler routes]} (webly-handler/create-ring-handler app-handler routes config-route websocket-routes)]
    (def ring-handler handler) ; needed by shadow-watch
    (write-target2 "routes" routes)
    handler))

(defn ensure-keyword [kw]
  (if (keyword? kw)
    kw
    (keyword kw)))

(defn start-webly [exts
                   {:keys [web-server]
                    :or {web-server {}}
                    :as config}
                   profile]
  (info "start-webly: " profile)
  (let [web-server (merge default/webserver web-server)
        server-type (if (= profile "watch")
                      :jetty
                      (ensure-keyword profile))
        port-config {; shadow-dev-http-port is set to shadow-dev http server port
                     ; when using "watch" profile. Otherwise it is 0.
                     :ports {:shadow-dev-http-port (shadow-dev-http-port config profile)
                             :webly-http-port (get-in web-server [:http :port])}}
        _ (info "webly port config: " port-config)
        config (merge config port-config)
        {:keys [routes frontend-config]} (configure config exts)
        config-route (create-config-routes frontend-config)
        websocket (start-websocket-server :jetty)
        websocket-routes (:bidi-routes websocket)
        app-handler (app-handler frontend-config)
        ring-handler (create-ring-handler app-handler routes config-route websocket-routes)
        webserver (web-server/start-webserver ring-handler web-server)
        shadow   (when (watch? profile)
                   (let [profile-full (setup-profile profile)]
                     (when (:bundle profile-full)
                       (build exts config profile-full))))]
    ; return config of started services (needed to stop)
    {:profile profile
     :webserver webserver
     :websocket websocket
     :shadow shadow}))

(defn stop-webly [{:keys [webserver _websocket shadow]}]
  (info "stopping webly..")
  (when webserver
    (web-server/stop webserver))
  (when shadow
    (stop-shadow shadow)))

;; BUILD

(defn webly-build [{:keys [config profile]}]
  (load-config! config)
  (let [config (get-in-config [])
        ext-config {:disabled-extensions (or (get-in config [:build :disabled-extensions]) #{})}
        exts (discover ext-config)
        {:keys [_routes frontend-config] :as _opts} (configure config exts)]
    (write-target2 :extensions-all (:extensions exts))
    (write-target2 :extensions-disabled (:extensions-disabled exts))
    (write-target2 "webly-build-config" config)
    (let [profile (setup-profile profile)]
      (when (:bundle profile)
        (build exts config profile))
      (when (:static? profile)
        (info "creating static page ..")
        (build-static (assoc frontend-config :prefix "./r/"))))))

(comment
  (def exts (discover {}))
  (get-extensions exts {:api-routes {}})

 ; 
  )

