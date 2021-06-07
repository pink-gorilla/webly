(ns demo.handler
  (:require
   [clojure.string]
   [clojure.pprint]
   [ring.util.response :as res]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]))

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


; ping 

(defn ping-handler [req]
  (clojure.pprint/pprint req)
  {:status 200 :body "test"})


(add-ring-handler :api/ping (wrap-api-handler ping-handler))




(defn snippet-handler [req]
  (clojure.pprint/pprint req)
  (println "snippet: " (shadow/compile :webly {:verbose false}))
  {:status 200 :body "snippet"})


(add-ring-handler :api/snippet (wrap-api-handler snippet-handler))

