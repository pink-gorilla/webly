(ns webly.spa.handler.routes.config
(:require
  [ring.util.response :refer [response]]
  [modular.webserver.middleware.api :refer [wrap-api-handler]]))

(defn create-config-routes [config frontend-routes]
  (let [config (select-keys config 
                            [ :static-main-path
                             :prefix 
                             :static?
                             :runner 
                             ; application specific keys
                             :google-analytics
                             :settings ; localstorage
                             :keybindings
                             :timbre/cljs 
                             :shadow ; todo remove. this does not look right.
                             :webly; todo remove. this does not look right.
                             :spa
                             ])
        config (assoc config :frontend-routes frontend-routes)
        config-handler (fn [_] (response config)) 
        config-handler-wrapped (wrap-api-handler config-handler)]
    {"config" config-handler-wrapped}))

