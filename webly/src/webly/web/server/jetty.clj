(ns webly.web.server.jetty
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.webserver.jetty :refer [run-jetty-server]]))

(defn jetty-ws-map [websocket jetty-ws-routes]
  (let [chsk-get-handler (get-in websocket [:bidi-routes "chsk" :get])]
  (->> (map (fn [route]
              [route chsk-get-handler])
            jetty-ws-routes)
       (into {}))))

(defn start-jetty
  [ring-handler websocket config]
  (run-jetty-server ring-handler
                    (jetty-ws-map websocket (:jetty-ws config) )
                    (assoc config :join? false)))

(defn stop-jetty
  [server]
  ;https://github.com/dharrigan/websockets/blob/master/src/online/harrigan/api/router.clj
  (info "stopping jetty-server..")
  (.stop server) ; stop is async
  (.join server)) ; so let's make sure it's really stopped!