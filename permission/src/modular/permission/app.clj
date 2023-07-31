(ns modular.permission.app
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config config-atom]]
   [modular.permission.websocket :as websocket]
   [modular.permission.user :refer [users-count]]
   [modular.permission.service :refer [add-permissioned-services]]
   ))

(def permission-active? (atom false))

(defn start-permissions []
  (let [c (users-count)
        has-users? (> c 0)
        services (get-in-config [:permission :service])
        has-services? (not (nil? services))]
    (if has-users?
      (do (info "websocket services are PERMISSION ENABLED! users #: " c)
          (reset! permission-active? true)
          (when has-services?
            (info "adding permissioned services: " services)
            (add-permissioned-services services)))
      (warn "websocket services are NOT PERMISSION enabled. everybody can use them!"))))


(defn service-authorized? [service-kw uid]
  (if @permission-active?
    (websocket/service-authorized? service-kw uid)
    true))

