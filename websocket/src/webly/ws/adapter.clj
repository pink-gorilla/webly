(ns webly.ws.adapter
  (:require
   [taoensso.timbre :as log :refer [debug info infof]]
   [taoensso.sente.packers.transit :as sente-transit]
  ; [taoensso.sente.server-adapters.undertow]
   [taoensso.sente.server-adapters.jetty9]
  ; [taoensso.sente.server-adapters.http-kit]
   [taoensso.sente  :as sente]
   [modular.encoding.transit :as e]
   [webly.ws.id :refer [get-sente-session-uid]]
   [webly.ws.msg-handler :refer [event-msg-handler]]))

#_(defn undertow []
    (debug "websocket mode: undertow.")
    (require '[taoensso.sente.server-adapters.undertow])

    taoensso.sente.server-adapters.undertow/get-sch-adapter)

(defn jetty []
  (debug "websocket mode: jetty.")
  (require '[taoensso.sente.server-adapters.jetty9])
  taoensso.sente.server-adapters.jetty9/get-sch-adapter)

#_(defn httpkit []
    (debug "websocket mode: httpkit")
    (require '[taoensso.sente.server-adapters.http-kit])
    taoensso.sente.server-adapters.http-kit/get-sch-adapter)

(defn get-adapter [server-type]
  (case server-type
    ;:undertow  (undertow)
    :jetty (jetty)
    ;:httpkit (httpkit)
    ;
    ))

(reset! sente/debug-mode?_ true) ; Uncomment for extra debug info

(defn ws-init! [server-type]
  (let [get-sch-adapter (get-adapter server-type)
        packer (sente-transit/get-transit-packer :json e/encode e/decode)
        chsk-server (sente/make-channel-socket-server!
                     (get-sch-adapter)
                     {:packer packer
                      :csrf-token-fn nil ; awb99; disable CSRF checking.
                      :user-id-fn get-sente-session-uid})
        {:keys [ch-recv send-fn connected-uids
                ajax-post-fn ajax-get-or-ws-handshake-fn]} chsk-server]
    {:chsk-send! send-fn
     :ch-chsk ch-recv
     :connected-uids connected-uids
     :ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn
     :ring-ajax-post ajax-post-fn}))

; router

(defonce router_ (atom nil))

(defn stop-router! []
  (when-let [stop-fn @router_] (stop-fn)))

(defn start-router! [conn]
  (let [{:keys [ch-chsk]} conn]
    (stop-router!)
    (reset! router_
            (sente/start-server-chsk-router! ch-chsk event-msg-handler))))
