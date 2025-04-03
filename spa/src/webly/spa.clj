(ns webly.spa
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [extension :refer [discover get-extensions]]
   [modular.config :refer [load-config! get-in-config]]
   [modular.writer :refer [write-edn-private]]
   [webserver.router :refer [create-handler]]
   [webserver.server :refer [start-webserver stop-webserver]]
   [shadowx.build.profile :refer [setup-profile]]
   [shadowx.build.core :refer [build]]
   [shadowx.build.shadow :refer [stop-shadow]]
   [shadowx.default :as shadow-default]
   [webly.spa.static :refer [build-static]]
   [webly.spa.service :refer [cljs-services]]
   [webly.spa.default :as default])
  (:gen-class))

(defn shadow-dev-http-port [config watch?]
  (if watch?
    ;(get-in config [:shadow :dev-http :port])
    (get-in shadow-default/shadow [:dev-http :port])
    0))

(defn create-ring-handler [services user-routes]
  (let [handler (create-handler services user-routes)]
    (def ring-handler handler) ; needed by shadow-watch
    handler))

(defn create-frontend-config [{:keys [spa prefix ports]
                               :or {spa {}
                                    prefix default/prefix}
                               :as config} exts]
  (let [spa (merge default/spa spa)
        frontend-config  {:prefix prefix
                          :spa spa
                          :cljs-services (cljs-services config exts)
                          :ports ports}]
    frontend-config))

(defn start-webly [{:keys [exts ctx]
                    :or {ctx {}} :as services}
                   {:keys [web-server routes]
                    :or {web-server {}
                         routes []}
                    :as config}
                   profile]
  (info (str "start-webly: [" profile "]"))
  (let [web-server (merge default/webserver web-server)
        watch?  (case profile
                  "watch" true
                  false)
        mode-config {; shadow-dev-http-port is set to shadow-dev http server port
                     ; when using "watch" profile. Otherwise it is 0.
                     :ports {:shadow-dev-http-port (shadow-dev-http-port config watch?)
                             :webly-http-port (get-in web-server [:http :port])
                             :profile profile
                             :mode :dynamic
                             :watch? watch?}
                     :mode :dynamic
                     :profile profile
                     :watch? watch?}
        config (merge config mode-config)
        frontend-config (create-frontend-config config exts)
        _ (write-edn-private :frontend-config frontend-config)
        ctx (assoc ctx :frontend-config frontend-config)
        services (assoc services :ctx ctx)
        ring-handler (create-ring-handler services routes)
        webserver (start-webserver ring-handler web-server)
        shadow   (if watch?
                   (let [_ (info "starting shadow-watch..")
                         profile-full (setup-profile profile)]
                     (when (:bundle profile-full)
                       (build exts config profile-full)))
                   (info "webly is serving a compiled cljs bundle."))]
    ; return config of started services (needed to stop)
    {:profile profile
     :webserver webserver
     :shadow shadow}))

(defn stop-webly [{:keys [webserver _websocket shadow]}]
  (info "stopping webly..")
  (when webserver
    (stop-webserver webserver))
  (when shadow
    (stop-shadow shadow)))

;; BUILD

(defn webly-build [{:keys [config profile]}]
  (load-config! config)
  (let [config (get-in-config [])
        ext-config {:disabled (or (get-in config [:extension :disabled]) #{})}
        exts (discover ext-config)
        frontend-config (create-frontend-config config exts)]
    (write-edn-private :extensions-all (:extensions exts))
    (write-edn-private :extensions-disabled (:extensions-disabled exts))
    (write-edn-private "webly-build-config" config)
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

