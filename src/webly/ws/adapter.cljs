(ns webly.ws.adapter
  (:require
   [taoensso.timbre :as timbre :refer-macros [tracef debug debugf infof warnf errorf trace]]
   [taoensso.sente :as sente :refer [cb-success?]]
   [taoensso.sente.packers.transit :as sente-transit] ;; Optional, for Transit encoding
   [webly.ws.id :refer [?csrf-token]]
   [webly.ws.msg-handler :refer [event-msg-handler]]))

;; see: https://github.com/ptaoussanis/sente/blob/master/example-project/src/example/client.cljs


(defn ws-init! [path port]
  (debug "connecting sente websocket.. " path port)
  (let [packer (sente-transit/get-transit-packer)
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
    (println "CSRF token detected in HTML, great!")
    (println "CSRF token NOT detected in HTML, default Sente config will reject requests")))

(defn start-router! [conn]
  (let [{:keys [ch-chsk]} conn]
    (stop-router!)
    (sente-csrf-warning)
    (reset! router_
            (sente/start-client-chsk-router! ch-chsk event-msg-handler))))


