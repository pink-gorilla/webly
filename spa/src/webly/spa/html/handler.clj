(ns webly.spa.html.handler
  (:require
   [clojure.string]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [bidi.ring]
   [modular.ws.service.middleware :refer [wrap-ws]]
   [webly.spa.html.page :refer [app-page-dynamic]]))

; CSRF TOKEN

(defn get-csrf-token []
  ; Another option:
  ;(:anti-forgery-token ring-req)] 
  (force *anti-forgery-token*))

;; APP

(defn make-handler-raw  [frontend-config]
  (fn [_req]
    (let [; csrf-token and session are sente requirements
          csrf-token (get-csrf-token)
        ;session  (sente-session-with-uid req)
          res (response/content-type
               {:status 200
              ;:session session
                :body (app-page-dynamic frontend-config csrf-token)}
               "text/html")]
    ;(response/header res "session" session)
      res)))

(defn app-handler [frontend-config]
  (-> (make-handler-raw frontend-config)
      wrap-ws))

