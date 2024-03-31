(ns webly.app.app
   (:require
    [taoensso.timbre :as timbre :refer [debug info warn error]]
    [extension :refer [discover write-service get-extensions]]
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

;; EXTENSION CONFIG

 (defn- get-api-routes [exts]
   (->> (get-extensions exts {:api-routes {}})
        (map :api-routes)
        (apply merge)))

  (defn- get-cljs-routes [exts]
   (->> (get-extensions exts {:cljs-routes {}})
        (map :cljs-routes)
        (apply merge)))

(defn- get-routes [exts]
  {:api (get-api-routes exts)
   :app (get-cljs-routes exts)})

 (defn- get-theme [exts]
   (let [themes (->> (get-extensions exts {:theme {:available {} :current {}}})
                     (map :theme))]
     {:available (reduce merge {} (map :available themes))
      :current (reduce merge {} (map :current themes))
      }))


;; HANDLER RELATED

(defn create-ring-handler [app-handler routes config-route websocket-routes]
  (let [{:keys [handler routes]} (webly-handler/create-ring-handler app-handler routes config-route websocket-routes)]
    (def ring-handler handler) ; needed by shadow-watch
    (write-status "routes" routes)
    handler))


(defn ensure-keyword [kw]
  (if (keyword? kw)
    kw
    (keyword kw)))

(defn start-webly [{:keys [web-server sente-debug? spa google-analytics prefix]
                    :or {sente-debug? false
                         web-server default/webserver
                         spa {}
                         google-analytics default/google-analytics
                         prefix default/prefix}
                    :as config}
                   server-type]
  (info "start-webly: " server-type)
  (let [ext-config {:disabled-extensions (or (get-in config [:build :disabled-extensions]) #{})}
        exts (discover ext-config)
        routes (get-routes exts)
        theme (get-theme exts)
        spa (merge default/spa spa)
        server-type (ensure-keyword server-type)
        permission (start-permissions)
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
  (let [config (get-in-config [])
        ext-config {:disabled-extensions (or (get-in config [:build :disabled-extensions]) #{})}
        exts (discover ext-config)]
    (write-service exts :extensions-all (:extensions exts))
    (write-service exts :extensions-disabled (:extensions-disabled exts))
    (write-status "webly-build-config" config)
    (let [profile (setup-profile profile)]
      (when (:bundle profile)
        (build exts config profile)))))


(comment
  (def exts (discover {}))
  (get-extensions exts {:api-routes {}})
  (get-api-routes exts)
  (get-cljs-routes exts)
  (get-routes exts)
  (get-theme exts)


 ; 
  )

