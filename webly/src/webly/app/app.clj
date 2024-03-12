(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [load-config! get-in-config]]
   [modular.writer :refer [write-status]]
   [modular.permission.service :refer [service-authorized?]]
   [modular.permission.app :refer [start-permissions]]
   [modular.ws.core :refer [start-websocket-server]]
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.web.server :as web-server]
   [webly.spa.handler.core :as webly-handler]
   [webly.spa.html.handler :refer [app-handler]]
   [webly.spa.handler.routes.config :refer [create-config-routes]]
   [webly.spa.default :as default])
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


(defn create-ring-handler [app-handler routes config-route websocket-routes]
  (let [routes (hack-routes-symbol routes)
        {:keys [handler routes]} (webly-handler/create-ring-handler app-handler routes config-route websocket-routes)]
    (def ring-handler handler) ; needed by shadow-watch
    (write-status "routes" routes)
    handler))

(defn ensure-keyword [kw]
  (if (keyword? kw)
    kw
    (keyword kw)))

(defn start-webly [{:keys [web-server sente-debug? spa theme google-analytics prefix]
                    :or {sente-debug? false
                         web-server default/webserver
                         spa {}
                         theme default/theme
                         google-analytics default/google-analytics
                         prefix default/prefix}
                    :as config}
                   server-type]
  (info "start-webly: " server-type)
  (let [spa (merge default/spa spa)
        server-type (ensure-keyword server-type)
        permission (start-permissions)
        routes (hack-routes-symbol (get-in config [:webly :routes]))
        user-config  (select-keys config
                                  [:static-main-path
                                   :static?
                                   :runner
                                  ; application specific keys
                                   :settings ; localstorage
                                   :keybindings
                                   :timbre/cljs
                                   :shadow ; todo remove. this does not look right.
                                   :webly; todo remove. this does not look right.
                                   ])
        frontend-config (merge user-config {:prefix prefix
                                            :spa spa
                                            :frontend-routes (:app routes)
                                            :theme theme
                                            :google-analytics google-analytics})
        config-route (create-config-routes frontend-config)
        websocket (start-websocket-server server-type sente-debug?)
        websocket-routes (:bidi-routes websocket)
        app-handler (app-handler spa theme prefix google-analytics)
        ring-handler (create-ring-handler app-handler routes config-route websocket-routes)
        webserver (if (watch? server-type)
                    (web-server/start web-server ring-handler websocket :jetty)
                    (web-server/start web-server ring-handler websocket (keyword server-type)))
        shadow   (when (watch? server-type)
                   (let [profile-full (setup-profile server-type)]
                     (when (:bundle profile-full)
                       (build config profile-full))))]
    ; return config of started services (needed to stop)
    {:profile server-type
     :permission permission
     :websocket websocket
     :webserver webserver
     :shadow shadow}))

(defn stop-webly [{:keys [webserver websocket shadow]}]
  (info "stopping webly..")
  (when webserver
    (web-server/stop webserver))
  (when shadow
    (stop-shadow shadow)))

;; BUILD

(defn webly-build [{:keys [config profile]}]
  (load-config! config)
  (let [config (get-in-config [])]
    (write-status "webly-build-config" config)
    (let [profile (setup-profile profile)]
      (when (:bundle profile)
        (build config profile)))))


;; TODO:

;; 2. webly.app.status.page.cljs uses get-in-config-cljs.

;; 4. remove the keyword registry!!!
;; 5. permission config.
;; 6. oauth2 config!

; 7. :static-main-path (static builds)