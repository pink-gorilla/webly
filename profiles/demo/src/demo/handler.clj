(ns demo.handler
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [ring.util.response :as res]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]))

(defn current-unix-time []
  (quot (System/currentTimeMillis) 1000))

(defn time-handler [req]
  (res/response {:current-time
                 (current-unix-time)}))

(add-ring-handler :api/time (wrap-api-handler time-handler))