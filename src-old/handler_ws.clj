
   [goldly.web.ws :refer [get-sente-session-uid sente-session-with-uid
                          ring-ajax-get-or-ws-handshake ring-ajax-post]]


; WEBSOCKET

(defn ws-token-handler-raw [req]
  (let [token {:csrf-token (get-csrf-token)}]
    (info "csrf token: " token)
    {:status 200
     :body (json/generate-string token)})) ; json must stay! sente has issues if middleware converts this

(defn ws-handshake-handler [req]
  (infof "ws-chsk rcvd: %s" req)
  (let [client-id  (get-in req [:params :client-id]) ; check if sente requirements are met
        uid (get-sente-session-uid req)
        res (ring-ajax-get-or-ws-handshake req)]
    (infof "ws-chsk client-id: %s uid: %s sente res: %s" client-id uid res)
    (info "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    res))

(defn ws-ajax-post-handler [req]
  (infof "/chsk post got: %s" req)
  (let [r (ring-ajax-post req)]
    (info "/chsk post result: " r)
    ;(info "ws csrf: " (get-in req [:session :ring.middleware.anti-forgery/anti-forgery-token]))
    r))

(def ws-token-handler
  (-> ws-token-handler-raw
      wrap-goldly))

(def ws-chsk-get
  (-> ws-handshake-handler
      wrap-goldly))

(def ws-chsk-post
  (-> ws-ajax-post-handler
      wrap-goldly))