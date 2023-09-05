(ns modular.ws.adapter
  (:require
   [taoensso.timbre :as log :refer [debug info infof]]
   [taoensso.sente.packers.transit :as sente-transit]
   [taoensso.sente  :as sente]
   [modular.encoding.transit :as e]
   [modular.ws.id :refer [get-sente-session-uid]]
   [modular.ws.msg-handler :refer [event-msg-handler]]
   [modular.ws.adapter.jetty :as jetty]))

(defn get-adapter [server-type]
  (case server-type
    :undertow (let [get-sch-adapter (requiring-resolve 'modular.ws.adapter.undertow/get-sch-adapter)]
                (get-sch-adapter))
    :jetty (jetty/get-sch-adapter)
    :httpkit (let [get-sch-adapter (requiring-resolve 'modular.ws.adapter.httpkit/get-sch-adapter)]
               (get-sch-adapter))
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
