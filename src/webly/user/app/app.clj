(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
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
   ))

(def profile-dev {:pref {:tenx true}
                  :config {:wrap-handler-reload true}})

(def profile-prod {:pref {:tenx false}
                   :config {:wrap-handler-reload false}})

(defn mode->profile [mode]
  (case mode
    "watch" profile-dev
    "release" profile-prod
    "build" profile-dev))

(defn setup-profile [mode]
  (load-config!)
  (let [profile (mode->profile mode)]
    (swap! prefs-atom merge (:pref profile))
    (info "prefs: " @prefs-atom)
    (swap! config-atom merge (:config profile))))

(defn webly-run! [mode lein-profile user-routes-api user-routes-app]
  (let [mode (or mode "watch")
        _ (setup-profile mode)
        routes-backend (make-routes-backend user-routes-app user-routes-api)
        routes-frontend (make-routes-frontend user-routes-app)
        wrap-handler-reload (get-in-config [:wrap-handler-reload])
        h (make-handler app-handler routes-backend routes-frontend)
        h (if wrap-handler-reload (wrap-dev h) h)]
    (info "webly starting - mode: " mode  " :wrap-handler-reload: " :wrap-handler-reload)
    (def handler h)
    (build-cli mode lein-profile "webly.user.app.app/handler" "demo.app")))