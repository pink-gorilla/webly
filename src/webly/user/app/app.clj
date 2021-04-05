(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [webly.config :refer [load-config! get-in-config]]
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

(defn webly-run! [mode lein-profile user-routes-api user-routes-app]
  (load-config!)
  (let [mode (or mode "watch")
        routes-backend (make-routes-backend user-routes-app user-routes-api)
        routes-frontend (make-routes-frontend user-routes-app)
        dev-mode? (get-in-config [:dev-mode])
        h (make-handler app-handler routes-backend routes-frontend)
        h (if dev-mode? (wrap-dev h) h)]
    (info "webly starting - mode: " mode  " dev?: " dev-mode?)
    (def handler h)
    (build-cli mode lein-profile "webly.user.app.app/handler" "demo.app")))