(ns webly.web.handler
  (:require
   [clojure.string]
   [taoensso.timbre :refer [info error]]
   [ring.util.response :as response]
   [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [webly.web.middleware :refer [wrap-webly]]
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

(def handler-registry
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! handler-registry assoc key handler))

(defn frontend? [routes-frontend handler-kw]
  (bidi/path-for routes-frontend handler-kw))

(defn get-handler
  [routes-frontend handler-kw]
  (info "get-handler:" handler-kw)
  (if (keyword? handler-kw)
    (if-let [handler (handler-kw @handler-registry)]
      handler
      (if (frontend? routes-frontend handler-kw)
        (do (info "get-handler: rendering web-app for frontend-route")
            app-handler)
        (do (error "handler-registry does not contain handler for: " handler-kw)
            nil)))
    handler-kw))

(defn make-handler
  [routes-backend routes-frontend]
  (bidi.ring/make-handler routes-backend (partial get-handler routes-frontend)))

(comment
  ; (bidi.ring/->ResourcesMaybe {:prefix "public"})
  (def routes-bidi
    ["/"  {"r" {true :n}}

     true :webly/not-found])

  (bidi/match-route routes-bidi "/r/bongo.txt" :request-method :get)
  (bidi/match-route routes-bidi "/r/8" :request-method :get)
  (bidi/match-route routes-bidi "/r998" :request-method :get))