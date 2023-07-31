(ns modular.ws.ws
  (:require
   [taoensso.timbre :as log :refer [debugf]]))

(defn send! [conn uid data]
  (let [{:keys [chsk-send!]} conn]
    (when-not (= uid :sente/nil-uid)
      (chsk-send! uid data))))

(defn send-all!
  [conn data]
  (let [{:keys [connected-uids]} conn
        uids (:any @connected-uids)
        nr (count uids)]
    (when (> nr 0)
      (debugf "Broadcasting event type: %s to %s clients" (first data) nr)
      (doseq [uid uids]
        (send! conn uid data)))))



