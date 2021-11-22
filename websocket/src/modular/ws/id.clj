(ns modular.ws.id)

(defn unique-id
  "Get a unique id for a session."
  []
  (str (java.util.UUID/randomUUID)))

(defn session-uid
  "Get session uuid from a request."
  [req]
  (get-in req [:session :uid]))

(defn get-sente-session-uid
  "Get session uuid from a request."
  [req]
  (or (get-in req [:session :uid])
      (unique-id)))

(defn sente-session-with-uid [req]
  (let [session (or (:session req) {})
        uid (or (get-sente-session-uid req)
                (unique-id))]
    (assoc session :uid uid)))