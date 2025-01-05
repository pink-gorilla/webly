(ns webly.spa.service.ws
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [cemerick.url :as url]
   [modular.ws.core :refer [start-websocket-client!]]
   [modular.ws.events] ; websocket re-frame events
   ))

(defn application-url
  "gets the current url, as a map"
  []
  (url/url (-> js/window .-location .-href)))

(defn server-port []
  (let [port (-> (application-url) :port)]
    (info "detected server port: " port)
    port))

(defn is-https? []
  (let [{:keys [protocol]} (application-url)]
    (= "https" protocol)))

(defn start-ws [{:keys [webly-http-port shadow-dev-http-port] :as ports}]
  ; sente websockets dont work on shadow-dev-http server.
  ; therefore we use webly-http port when browser uses shadow-dev-http 
  (let [protocol (if (is-https?)
                   :https
                   :http)
        route "/api/chsk"
        detected-port (server-port)
        port (cond
               ; https always on 443
               (is-https?)
               443

               ; if the port matches the shadow-dev-http port, we are on shadow-dev, so we redirect
               (= detected-port shadow-dev-http-port)
               (do
                 (info "this is a shadow-cljs-dev session. connecting websocket to WEBLY SERVER on port: " shadow-dev-http-port)
                 webly-http-port)

               ; in all other cases we use the detected port
               :else
               detected-port)]
    (if port
      (do (info "connecting websocket protocol: " protocol "route: " route " port: " port)
          (start-websocket-client! protocol route port))
      (error "WEBSOCKET cannot connect. port nil! webly-http-port: " webly-http-port " shadow-port: " shadow-dev-http-port))))


