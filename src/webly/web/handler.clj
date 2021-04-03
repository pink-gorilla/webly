(ns webly.web.handler
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]
   [ring.util.response :as response]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [webly.web.app :refer [app-handler]]))

(defn html-response [html]
  (response/content-type
   {:status 200
    :body html}
   "text/html"))

(def handler-registry
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! handler-registry assoc key handler))

(defn frontend? [routes-frontend handler-kw]
  (try
    (bidi/path-for routes-frontend handler-kw)
    (catch Exception e
      (error "exception in determining bidi/path-for for " handler-kw " ex:" e)
      true)))

(defn get-handler
  [routes-frontend handler-kw]
  (when (keyword? handler-kw) ;resources havea wrapped handler
    (debug "get-handler:" handler-kw))
  (if (keyword? handler-kw)
    (if-let [handler (handler-kw @handler-registry)]
      handler
      (if (frontend? routes-frontend handler-kw)
        (do (debug "get-handler: rendering web-app for frontend-route")
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