(ns webly.ws.handler
  (:require
   [webly.web.handler :refer [add-ring-handler]]
   [webly.ws.handler :refer [ws-token-handler ws-chsk-get ws-chsk-post]]))

(def routes-ws
  {"token" :ws/token            ;  #'ws-token-handler
   "chsk" {:get :ws/chsk-get    ;  #'ws-chsk-get
           :post :ws/chsk-post  ;   #'ws-chsk-post
           }})

(add-ring-handler :ws/token ws-token-handler)
(add-ring-handler :ws/chsk-get ws-chsk-get)
(add-ring-handler :ws/chsk-post ws-chsk-post)

