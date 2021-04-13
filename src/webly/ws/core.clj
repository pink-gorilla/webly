(ns webly.ws.core
  (:require
   [taoensso.timbre :as log :refer [error]]
   [webly.ws.adapter :refer [ws-init! start-router!]]
   [webly.ws.handler :refer [add-ws-handler]]
   [webly.ws.ws :as ws]))

(def c (atom nil))

(defn init-ws! [server-type]
  (let [conn (ws-init! server-type)]
    (start-router! conn)
    (add-ws-handler conn)
    (reset! c conn)))

(defn send-all! [data]
  (if @c
    (ws/send-all! @c data)
    (error "ws/send-all - not setup. data: " data)))

(comment
  ;(println "clients: " @connected-uids)

  (send-all! [:pinkie/broadcast {:a 13}])

  ;
  )