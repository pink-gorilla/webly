(ns modular.permission.websocket
  (:require
    [taoensso.timbre :as timbre :refer [debug info infof warn error]]
    [modular.permission.service :as service]
    [modular.config :refer [get-in-config]]
  ))

(def connected-users (atom {}))

(defn set-user! [uid user]
  (infof "ws uid: %s =>  user: %s" uid user)
  (swap! connected-users assoc uid user)
  (info "ws users: " (pr-str @connected-users)))

(defn get-user [uid]
  (get @connected-users uid))


(defn service-authorized? [service-kw uid]
  ; uid is the id from the websocket.
  (let [user-id  (get-user uid)]
    (service/service-authorized? service-kw user-id)))
