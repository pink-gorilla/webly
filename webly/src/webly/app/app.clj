(ns webly.app.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [load-config! config-atom]]
   [modular.writer :refer [write-status]]
   [webly.build.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.build.shadow :refer [stop-shadow]]
   [webly.web.server :as web-server]
   [webly.spa.handler.core :as webly-handler]
   [modular.permission.service :refer [service-authorized?]]
   [modular.permission.app :refer [start-permissions]])
  (:gen-class))

(defn watch? [profile-name]
  (case profile-name
    "watch" true
    "watch2" true
    false))

;; HANDLER RELATED

(defn hack-routes-symbol [routes]
  (if (symbol? routes)
    (let [routes (-> routes requiring-resolve var-get)]
      (swap! config-atom assoc-in [:webly :routes] routes)
      routes)
    routes))

(defn create-ring-handler [routes]
  (let [routes (hack-routes-symbol routes)
        {:keys [handler routes]} (webly-handler/create-ring-handler routes)]
    (def ring-handler handler) ; needed by shadow-watch
    (write-status "routes" routes)
    handler))

(defn start-webly [config server-type]
  (info "start-webly: " server-type)
  (start-permissions)
  (let [ring-handler (let [routes (get-in config [:webly :routes])]
                       (create-ring-handler routes))
        webserver  (let [webserver-config (get-in config [:webly/web-server])]
                     (if (watch? server-type)
                       (web-server/start webserver-config ring-handler :jetty)
                       (web-server/start webserver-config ring-handler (keyword server-type))))
        shadow   (when (watch? server-type)
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
