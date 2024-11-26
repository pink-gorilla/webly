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
  (-> (application-url) :port))

(defn is-https? []
  (let [{:keys [protocol]} (application-url)]
    (= "https" protocol)))

(defn start-ws [{:keys [webly-http-port shadow-dev-http-port] :as ports}]
  ; sente websockets dont work on shadow-dev-http server.
  ; therefore we use webly-http port when browser uses shadow-dev-http 
  (let [port (server-port)
        served-by-shadow-dev? (= port shadow-dev-http-port)
        port (if served-by-shadow-dev?
               webly-http-port
               port)
        route "/api/chsk"]
    (if served-by-shadow-dev?
      (info "this is a shadow-cljs-dev session. connecting websocket to WEBLY SERVER route: " route " port: " port)
      (info "connecting websocket route: " route " port: " port))
    (if port
      (start-websocket-client! route port)
      (error "WEBSOCKET cannot connect. port nil! webly-http-port: " webly-http-port " shadow-port: " shadow-dev-http-port))))


