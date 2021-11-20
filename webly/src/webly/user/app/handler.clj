(ns webly.user.app.handler
  (:require
   [clojure.string]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [bidi.ring]
   [webly.web.middleware :refer [wrap-webly]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.user.app.views :refer [app-page]]))

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

(defn app-handler-raw [req]
  (let [; csrf-token and session are sente requirements
        csrf-token (get-csrf-token)
        ;session  (sente-session-with-uid req)
        res (response/content-type
             {:status 200
              ;:session session
              :body (app-page csrf-token)}
             "text/html")]
    ;(response/header res "session" session)
    res))

(def app-handler
  (-> app-handler-raw
      wrap-webly))

(add-ring-handler :webly/app-bundle app-handler)


