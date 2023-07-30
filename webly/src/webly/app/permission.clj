(ns webly.app.permission
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.config :refer [get-in-config config-atom]]
   [modular.ws.msg-handler :refer [permission-fn-a]]
   [modular.oauth2.local.handler :refer [service-authorized?]]))

(defn start-permissions []
  (if (and (get-in-config [:permission])
           (get-in-config [:users]))
    (do (info "starting webly ws services with PERMISSIONS enabled.")
        (reset! permission-fn-a service-authorized?))
    (warn "webly ws services are NOT PERMISSION enabled.")))
