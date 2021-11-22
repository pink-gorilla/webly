(ns modular.ws.adapter
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debug debugf infof warn warnf errorf trace]]
   [taoensso.sente :as sente :refer [cb-success?]]
   [taoensso.sente.packers.transit :as sente-transit] ;; Optional, for Transit encoding
   [modular.encoding.transit :as e]
   [modular.ws.msg-handler :refer [event-msg-handler]]))

;; see: https://github.com/ptaoussanis/sente/blob/master/example-project/src/example/client.cljs

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

(defn ws-init! [path port]
  (debug "connecting sente websocket.. " path port)
  (let [packer (sente-transit/get-transit-packer :json e/encode e/decode)
        opts {:type :auto  ; :ajax
              :packer packer}
        opts (if port
               (assoc opts :port port)
               opts)
        {:keys [chsk ch-recv send-fn state]} (sente/make-channel-socket-client!
                                              path; Must match server Ring routing URL
                                              ?csrf-token
                                              opts)]
    {:chsk chsk
     :ch-chsk ch-recv
     :chsk-send! send-fn
     :chsk-state state}))

; router

(defonce router_ (atom nil))

(defn  stop-router! [] (when-let [stop-f @router_] (stop-f)))

(defn sente-csrf-warning []
  (if ?csrf-token
    (debug "CSRF token detected in HTML, great!")
    (warn "CSRF token NOT detected in HTML, default Sente config will reject requests")))

(defn start-router! [conn]
  (let [{:keys [ch-chsk]} conn]
    (stop-router!)
    (sente-csrf-warning)
    (reset! router_
            (sente/start-client-chsk-router! ch-chsk event-msg-handler))))



