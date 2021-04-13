(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [webly.config :refer [get-in-config config-atom]]
   [webly.profile :refer [setup-profile server?]]
   [webly.build.core :refer [build]]
   [webly.web.server :refer [run-server]]
   [webly.web.handler :refer [make-handler]]
   [webly.web.middleware-dev :refer [wrap-dev]]
   [webly.user.app.handler :refer [app-handler]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]

   ; side-effects
   [webly.user.config.handler]   ; handler: config
   [webly.user.oauth2.handler]   ; handler: oauth2, github-auth-token
   [webly.user.markdown.handler] ; handler : md docs
   [webly.user.app.keybindings]))

(defn resolve-name [str]
  (let [sym (symbol str)]
    (when (nil? sym)
      (error "could not resolve route symbol: " str))
    (var-get (resolve sym))))

(defn create-ring-handler
  "creates a ring-handler
   uses configuration in webly-config to do so
   the def statement defines a variable in this ns. This is used by shadow-cljs to resolve the handler.
   "
  []
  (info "webly creating ring handler.. ")
  (let [{:keys [user-routes-app user-routes-api]} (get-in-config [:webly])
        _ (info "api-routes-sym:" user-routes-api "app-routes-sym:" user-routes-app)
        user-routes-app (resolve-name user-routes-app)
        user-routes-api (resolve-name user-routes-api)
        _ (info "user-api-routes:" user-routes-api "user-app-routes:" user-routes-app)
        routes-backend (make-routes-backend user-routes-app user-routes-api)
        routes-frontend (make-routes-frontend user-routes-app)
        _ (info "all-api-routes:" routes-backend "all-app-routes:" routes-frontend)
        wrap-handler-reload (get-in-config [:wrap-handler-reload])
        h (make-handler app-handler routes-backend routes-frontend)
        h (if wrap-handler-reload (wrap-dev h) h)]
    (def ring-handler h)))

(defn run-server-p [profile]
  (info "webly starting webserver : " (:server profile))
  (create-ring-handler)
  (run-server ring-handler profile))

(defn build-p [profile]
  (info "webly starting build:  " (:bundle profile))
  (build profile))

(defn webly-run! [profile-name]
  (let [profile (setup-profile profile-name)]
    (if (:server profile)
      (run-server-p profile)
      (info "not running web server"))
    (if (:bundle profile)
      (build-p profile)
      (info "not building bundle."))))