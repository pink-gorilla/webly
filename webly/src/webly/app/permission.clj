(ns webly.app.permission
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config config-atom]]
   [modular.ws.msg-handler :refer [permission-fn-a]]
   [modular.permission.user :refer [users-count]]
   [modular.permission.websocket :refer [service-authorized?]]
   [modular.permission.service :refer [add-permissioned-services]]))

(defn start-permissions []
  (let [c (users-count)
        has-users? (> c 0)
        services (get-in-config [:permission :service])
        has-services? (not (nil? services))]
    (if (and has-users? has-services?)
      (do (info "PERMISSIONS enabled. users #: " c)
          (add-permissioned-services services)
          (reset! permission-fn-a service-authorized?))
      (warn "webly ws services are NOT PERMISSION enabled."))))

