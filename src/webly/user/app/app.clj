(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [webly.config :refer [load-config! get-in-config config-atom]]
   [webly.prefs :refer [prefs-atom]]
   [webly.build :refer [build-cli]]
   [webly.web.handler :refer [make-handler]]
   [webly.web.middleware-dev :refer [wrap-dev]]
   [webly.user.app.handler :refer [app-handler]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]
   ; side-effects
   [webly.user.config.handler]   ; handler: config
   [webly.user.oauth2.handler]   ; handler: oauth2, github-auth-token
   [webly.user.markdown.handler] ; handler : md docs
   [webly.user.app.keybindings]))

(def profile-dev {:pref {:tenx true}
                  :config {:wrap-handler-reload false}}) ; true

(def profile-prod {:pref {:tenx false}
                   :config {:wrap-handler-reload false}})

(defn mode->profile [mode]
  (case mode
    "watch" profile-dev
    "build" profile-dev
    "release" profile-prod
    "ci" profile-prod))

(defn setup-profile [mode]
  (load-config!)
  (let [profile (mode->profile mode)]
    (swap! prefs-atom merge (:pref profile))
    (info "prefs: " @prefs-atom)
    (swap! config-atom merge (:config profile))))

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

(defn webly-run! [mode]
  (let [mode (or mode "watch")
        _ (setup-profile mode)]
    (info "webly starting - mode: " mode)
    (when (= mode "watch")
      (create-ring-handler))
    (build-cli mode)))