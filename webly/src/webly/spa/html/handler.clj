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

(defn unique-id
  "Get a unique id."
  []
  (str (java.util.UUID/randomUUID)))

;; APP

(defn make-handler-raw  [webly-config]
  (fn [req]
    (let [; csrf-token and session are sente requirements
          csrf-token (get-csrf-token)
        ;session  (sente-session-with-uid req)
          res (response/content-type
               {:status 200
              ;:session session
                :body (app-page-dynamic webly-config csrf-token)}
               "text/html")]
    ;(response/header res "session" session)
      res)))

  (defn app-handler [webly-config]
    (-> (make-handler-raw webly-config)
        wrap-ws))

