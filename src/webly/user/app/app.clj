(ns webly.user.app.app
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [webly.config :refer [load-config!]]
   [webly.build :refer [build-cli]]
   [webly.web.handler :refer [make-handler]]
   [webly.user.app.handler :refer [app-handler]]
   [webly.user.app.routes :refer [make-routes-frontend make-routes-backend]]
   ; side-effects
   [webly.user.config.handler]   ; handler: config
   [webly.user.oauth2.handler]   ; handler: oauth2, github-auth-token
   [webly.user.markdown.handler] ; handler : md docs
   ))

(defn webly-run! [mode lein-profile user-routes-api user-routes-app]
  (let [mode (or mode "watch")
        routes-backend (make-routes-backend user-routes-app user-routes-api)
        routes-frontend (make-routes-frontend user-routes-app)]
    (info "demo starting mode: " mode)
    (load-config!)
    (def handler (make-handler app-handler routes-backend routes-frontend))
    (build-cli mode lein-profile  "webly.user.app.app/handler" "demo.app")))