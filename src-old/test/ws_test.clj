


; websocket

#_(deftest ws-routes []
  (is (= #'goldly.web.handler/ws-token-handler (handler GET  "/token")))
  (is (= #'goldly.web.handler/ws-chsk-get (handler GET  "/chsk")))
  (is (= #'goldly.web.handler/ws-chsk-post (handler POST "/chsk"))))