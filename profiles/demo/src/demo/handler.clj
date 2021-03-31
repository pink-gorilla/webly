(ns demo.handler
  (:require
   [ring.util.response :as res]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.user.markdown.handler :refer [handler-md-files]]
   ))

; test

(defn test-handler [req]
  {:status 200 :body "test"})

(add-ring-handler :api/test test-handler)

; time

(defn current-unix-time []
  (quot (System/currentTimeMillis) 1000))

(defn time-handler [req]
  (res/response {:current-time
                 (current-unix-time)}))

(add-ring-handler :api/time (wrap-api-handler time-handler))

(add-ring-handler :api/md (wrap-api-handler time-handler))

(add-ring-handler :api/md (wrap-api-handler handler-md-files))


