(ns modular.ws.handler
  (:require
   [taoensso.timbre :as log :refer [debug debugf info infof]]
   [cheshire.core :as json]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [modular.ws.id :refer [get-sente-session-uid]]
   [modular.ws.middleware :refer [wrap-ws]]))

; CSRF TOKEN

(defn get-csrf-token []
  ; Another option:
  ;(:anti-forgery-token ring-req)] 
  (force *anti-forgery-token*))

; WEBSOCKET

(defn ws-token-handler-raw [req]
  (let [token {:csrf-token (get-csrf-token)}]
    (info "sending csrf token: " token)
    {:status 200
     :body (json/generate-string token)})) ; json must stay! sente has issues if middleware converts this

(defn ws-handshake-handler [conn req]
  (debugf "ws/chsk GET: %s" req)
  (let [{:keys [ring-ajax-get-or-ws-handshake]} conn
        client-id  (get-in req [:params :client-id]) ; check if sente requirements are met
        uid (get-sente-session-uid req)
        res (ring-ajax-get-or-ws-handshake req)]
    (infof "ws-chsk client-id: %s uid: %s csrf: %s"
           client-id uid
           (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    (debug res)
    res))

(defn ws-ajax-post-handler [conn req]
  (infof "ws/chsk POST: %s" req)
  (let [{:keys [ring-ajax-post]} conn
        res (ring-ajax-post req)]
    (info "/chsk post result: " res)
    ;(info "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    res))

(defn add-ws-handler [conn]
  (add-ring-handler :ws/token (wrap-ws ws-token-handler-raw))
  (add-ring-handler :ws/chsk-get (wrap-ws (partial ws-handshake-handler conn)))
  (add-ring-handler :ws/chsk-post (wrap-ws (partial ws-ajax-post-handler conn))))

