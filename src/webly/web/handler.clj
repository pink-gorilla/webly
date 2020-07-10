(ns webly.web.handler
  (:require
   [clojure.string]
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf errorf]]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [bidi.ring]
   [webly.user.auth.middleware :refer [wrap-oauth]]
   [webly.web.middleware :refer [wrap-goldly]]
   [webly.web.views :refer [app-page]]))

(defn html-response [html]
  (response/content-type
   {:status 200
    :body html}
   "text/html"))

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
        session  67 ;  (sente-session-with-uid req)
        res (response/content-type
             {:status 200
              :session session
              :body (app-page csrf-token)}
             "text/html")]
    (response/header res "session" session)))

(def app-handler
  (-> app-handler-raw
      wrap-goldly))

; oauth2

(def handler-auth
  (-> webly.user.auth.middleware/handler-auth
      wrap-oauth))

(def ring-handler
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! ring-handler assoc key handler))

(defn route->handler
  ([]
   (info "->handler no args ..")
   nil)
  ([handler-kw & args]
   (info "route handler:" handler-kw " args:" args)
   (if-let [handler (handler-kw @ring-handler)]
     handler
     app-handler)))

; handler is used by shadow-cljs
(defn make-handler
  [bidi-routes]
  (bidi.ring/make-handler bidi-routes route->handler))

(defn add-webly-default-handler []
  (-> ring-handler
      (swap! assoc :webly/oauth2 handler-auth)
      ;(swap! assoc :webly/not-found not-found-handler)
      ))
