(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [extension :refer [discover get-extensions]]
   [modular.config :refer [load-config! get-in-config]]
   [modular.ws.core :refer [start-websocket-server]]
   [webly.helper :refer [write-target2]]
   [webly.build.profile :refer [setup-profile]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.build.static :refer [build-static]]
   [webly.web.server :as web-server]
   [webly.spa.handler.core :as webly-handler]
   [webly.spa.html.handler :refer [app-handler]]
   [webly.spa.handler.routes.config :refer [create-config-routes]]
   [webly.spa.config :refer [configure]]
   [webly.spa.default :as default])
  (:gen-class))

(defn watch? [profile-name]
  (case profile-name
    "watch" true
    "watch2" true
    false))

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
                   {:keys [web-server sente-debug?]
                    :or {sente-debug? false
                         web-server default/webserver}
                    :as config}
                   server-type]
  (info "start-webly: " server-type)
  (let [server-type (ensure-keyword server-type)
        {:keys [routes frontend-config]} (configure config exts)
        config-route (create-config-routes frontend-config)
        websocket (start-websocket-server server-type sente-debug?)
        websocket-routes (:bidi-routes websocket)
        app-handler (app-handler frontend-config)
        ring-handler (create-ring-handler app-handler routes config-route websocket-routes)
        webserver (if (watch? server-type)
                    (web-server/start web-server ring-handler websocket :jetty)
                    (web-server/start web-server ring-handler websocket (keyword server-type)))
        shadow   (when (watch? server-type)
                   (let [profile-full (setup-profile server-type)]
                     (when (:bundle profile-full)
                       (build exts config profile-full))))]
    ; return config of started services (needed to stop)
    {:profile server-type
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

