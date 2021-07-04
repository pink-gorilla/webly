(ns webly.web.handler
  (:require
   [clojure.string]
   [taoensso.timbre :refer [debug info error]]
   [ring.util.response :as response]
   [bidi.bidi :as bidi]
   [bidi.ring]))

#_(defn html-response [html]
    (response/content-type
     {:status 200
      :body html}
     "text/html"))

(defonce handler-registry
  (atom {}))

(defn add-ring-handler [key handler]
  (swap! handler-registry assoc key handler))

; not found handler

(def not-found-body "<h1>  bummer, not found </h1")

(defn not-found-handler [req]
  (response/not-found not-found-body))

(add-ring-handler :webly/not-found not-found-handler)

; server request serving

(defn get-handler-backend
  [handler-kw]
  (when (keyword? handler-kw) ;resources have a wrapped handler
    (debug "get-handler-backend:" handler-kw))
  (if (keyword? handler-kw)
    (if-let [handler (handler-kw @handler-registry)]
      handler
      (do (error "handler-registry does not contain handler for: " handler-kw)
          nil))
    handler-kw))

(defn get-handler-frontend [app-handler routes-frontend path req]
  (debug "frontend?" path)
  (let [{:keys [handler route-params] :as match-context}
        (bidi/match-route* routes-frontend path req)]
    (when handler
      (info "serving app-bundle initial location: " handler)
      app-handler)))

(defn make-handler
  "Create a Ring handler from the route definition data
  structure. Matches a handler from the uri in the request, and invokes
  it with the request as a parameter."
  [app-handler routes-backend routes-frontend]
  (assert routes-backend "Cannot create a Ring handler with a nil Route(s) parameter")
  (fn [{:keys [uri path-info] :as req}]
    (let [path (or path-info uri)
          {:keys [handler route-params] :as match-context}
          (bidi/match-route* routes-backend path req)
          handler-f (if handler
                      (get-handler-backend handler)
                      (get-handler-frontend app-handler routes-frontend path req))
          handler-f (if handler-f
                      handler-f
                      not-found-handler)]
      (when handler-f
        (bidi.ring/request
         handler-f
         (-> req
             (update-in [:params] merge route-params)
             (update-in [:route-params] merge route-params))
         (apply dissoc match-context :handler (keys req)))))))

; testing:

(comment
  ; (bidi.ring/->ResourcesMaybe {:prefix "public"})
  (def routes-bidi
    ["/"  {"r" {true :n}}

     true :webly/not-found])

  (bidi/match-route routes-bidi "/r/bongo.txt" :request-method :get)
  (bidi/match-route routes-bidi "/r/8" :request-method :get)
  (bidi/match-route routes-bidi "/r998" :request-method :get))