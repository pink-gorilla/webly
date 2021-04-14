(ns webly.ws.core
  (:require
   [taoensso.timbre :as log :refer [error info]]
   [clojure.core.async :as async  :refer [<! <!! >! >!! put! chan go go-loop]]
   [webly.ws.adapter :refer [ws-init! start-router!]]
   [webly.ws.handler :refer [add-ws-handler]]
   [webly.ws.ws :as ws]))

(def c (atom nil))

(defn log-on-conn-chg [old new]
  (info "conn chg: old:" old "new: " new))

(def on-conn-chg (atom log-on-conn-chg))

(defn- watch-conn [conn]
  (let [{:keys [connected-uids]} conn]
    (add-watch connected-uids :connected-uids
               (fn [_ _ old new]
                 (when (not= old new)
                   (let [notify @on-conn-chg]
                     (notify old new)))))))

(defn init-ws! [server-type]
  (let [conn (ws-init! server-type)]
    (watch-conn conn)
    (start-router! conn)
    (add-ws-handler conn)
    (reset! c conn)))

(defn send! [uid data]
  (if @c
    (ws/send! @c uid data)
    (error "ws/send - not setup. data: " data)))

(defn send-all! [data]
  (if @c
    (ws/send-all! @c data)
    (error "ws/send-all - not setup. data: " data)))

(comment
  ;(println "clients: " @connected-uids)

  (send-all! [:pinkie/broadcast {:a 13}])

  ;
  )