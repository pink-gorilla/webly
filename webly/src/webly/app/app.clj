(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [load-config! config-atom]]
   [modular.system :as system]
   [modular.writer :refer [write-status]]
   [modular.oauth2.handler] ; side-effects
   ;[modular.oauth2.middleware :refer [print-oauth2-config]]
   [modular.ws.core :refer [init-ws!]]
   [modular.permission.service :refer [service-authorized?]]
   [modular.oauth2] ; side-effects
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.web.server :as web-server]
   [webly.web.handler :refer [make-handler]]
   [webly.app.handler :refer [app-handler]]
   [webly.app.routes :refer [make-routes-frontend make-routes-backend]]
   [modular.permission.app :refer [start-permissions]])
  (:gen-class))

(defn create-ring-handler
  "creates a ring-handler
   uses configuration in webly-config to do so
   the def statement defines a variable in this ns. This is used by shadow-cljs to resolve the handler.
   "
  [routes]
  (info "create-ring-handler: " routes)
  (let [routes-backend (make-routes-backend (:app routes) (:api routes))
        routes-frontend (make-routes-frontend (:app routes))
        ;_ (info "all-api-routes:" routes-backend "all-app-routes:" routes-frontend)
        h (make-handler app-handler routes-backend routes-frontend)]
    (write-status "routes" {:frontend routes-frontend :backend routes-backend})
    (def ring-handler h) ; needed by shadow-watch
    h))

(defn watch? [profile-name]
  (case profile-name
    "watch" true
    "watch2" true
    false))

(defn hack-routes-symbol [routes]
  (if (symbol? routes)
    (let [routes (-> routes requiring-resolve var-get)]
      (swap! config-atom assoc-in [:webly :routes] routes)
      routes)
    routes))

(defn start-webly [config server-type]
  (info "start-webly: " server-type)
  (start-permissions)
  (let [ring-handler (let [routes (get-in config [:webly :routes])
                           routes (hack-routes-symbol routes)]
                       (create-ring-handler routes))
        webserver  (if (watch? server-type)
                     (web-server/start ring-handler :jetty)
                     (web-server/start ring-handler (keyword server-type)))
        shadow   (when (watch? server-type)
                  ; (init-ws! :undertow)
                   (let [profile-full (setup-profile server-type)]
                     (when (:bundle profile-full)
                       (build profile-full))))]
    ; return config of started services (needed to stop)
    {:profile server-type
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
