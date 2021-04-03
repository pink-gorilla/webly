(ns webly.web.app
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [bidi.bidi :as bidi]
   [bidi.ring]

   [webly.web.middleware :refer [wrap-webly]]
   [webly.web.views :refer [app-page]]))

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