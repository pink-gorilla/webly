(ns webly.web.middleware
  "a middleware takes a handler, and wraps a middleware around it.
   It is handler transformation, not routing related."
  (:require
   [clojure.string]
   [ring.middleware.cors :refer [wrap-cors]]
   [ring.middleware.cljsjs :refer [wrap-cljsjs]]
   [ring.middleware.gzip :refer [wrap-gzip]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   [ring.middleware.session :refer [wrap-session]]
   ;[ring.middleware.format :refer [wrap-restful-format]]
   [muuntaja.middleware :refer [wrap-format]]
   [ring.middleware.json :refer [wrap-json-response]]))


;from clojurewb - good example for middleware for websocket requests


#_(defn wrap-formats2 [handler]
    (let [wrapped (-> handler wrap-params (wrap-format formats/instance))]
      (fn [request]
      ;; disable wrap-formats for websockets
      ;; since they're not compatible with this middleware
        ((if (:websocket? request) handler wrapped) request))))

#_(defn wrap-api-handler
    "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
    [handler]
    (-> handler
        (wrap-defaults api-defaults)
        (wrap-restful-format :formats [:json :transit-json :edn])))

(defn wrap-api-handler ; from gorilla-explore
  "a wrapper for restful API calls"
  [handler]
  (-> handler ; middlewares execute from bottom -> up
      ;(wrap-defaults api-defaults)
      (wrap-keyword-params)
      (wrap-params)
      (wrap-format) ; muuntaja https://github.com/metosin/muuntaja
      #_(wrap-restful-format :formats [:json
                                     ;:json-kw 
                                       :transit-json
                                       :edn])
      ;(wrap-json-response)
      ;(wrap-gzip)
      ))

(defn wrap-stateful-api-handler
  "a wrapper for JSON API calls
   from pinkgorilla notebook
   "
  [handler]
  (-> handler ; middlewares execute from bottom -> up
      (wrap-anti-forgery)

      (wrap-defaults api-defaults)
      (wrap-keyword-params)
      (wrap-params)
      ;(wrap-cljsjs)
      (wrap-format) ; muuntaja
      ;(wrap-restful-format :formats [:json :transit-json :edn])
      (wrap-gzip)))

#_(defn wrap-ws [handler]
    (-> handler
        #_(wrap-defaults
           (-> site-defaults
               (assoc-in [:security :anti-forgery] true)))

        (wrap-defaults api-defaults)
      ;(wrap-api-handler)
      ;(wrap-cors-handler)
        (wrap-cljsjs) ; oz
      ;(wrap-session)
      ;(wrap-json-response)
        (wrap-gzip))) ;oz 

(defn wrap-webly [handler]
  (-> handler
      (wrap-defaults site-defaults)
      (wrap-session)
      wrap-keyword-params
      wrap-params
      (wrap-cljsjs)
      (wrap-gzip)))

;; Add necessary Ring middleware:
;      ring.middleware.keyword-params/wrap-keyword-params
;ring.middleware.params/wrap-params
