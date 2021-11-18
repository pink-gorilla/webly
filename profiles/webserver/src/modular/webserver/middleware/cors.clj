(ns modular.webserver.middleware.cors
  (:require
   ;[ring.middleware.cors :refer [wrap-cors]]
   [ring.util.response :refer [response]]))

(defn wrap-allow-cross-origin
  "Middleware function to allow cross origin requests from browsers.
   When a browser attempts to call an API from a different domain, it makes an OPTIONS request first to see the server's
   cross origin policy.  So, in this method we return that when an OPTIONs request is made.
   Additionally, for non OPTIONS requests, we need to just returm the 'Access-Control-Allow-Origin' header or else
   the browser won't read the data properly.
   The above notes are all based on how Chrome works. "
  ([handler]
   (wrap-allow-cross-origin handler "*"))
  ([handler allowed-origins]
   (fn [request]
     (if (= (request :request-method) :options)
       (-> (response)                     ; Don't pass the requests down, just return what the browser needs to continue.
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins)
           (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,POST,DELETE")
           (assoc-in [:headers "Access-Control-Allow-Headers"] "X-Requested-With,Content-Type,Cache-Control,Origin,Accept,Authorization")
           (assoc :status 200))
       (-> (handler request)         ; Pass the request on, but make sure we add this header for CORS support in Chrome.
           (assoc-in [:headers "Access-Control-Allow-Origin"] allowed-origins))))))
