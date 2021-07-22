(ns demo.handler
  (:require
   [clojure.string]
   [clojure.pprint]
   [clojure.java.io :as io]
   [taoensso.timbre :as log :refer [tracef debugf info infof warnf error errorf]]

   [ring.util.response :as res]
   [ring.util.io :as ring-io]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.date :refer [now now-local now-unix]]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   [bidi.bidi :as bidi]))

; test

(defn test-handler [req]
  {:status 200 :body "test"})

(add-ring-handler :api/test test-handler)

; time

(defn time-handler [req]
  (res/response {:unix
                 (now-unix)}))

(add-ring-handler :api/time (wrap-api-handler time-handler))

(defn time-java-handler [req]
  (res/response {:unix (now-unix)
                 :java (now)
                 :local (now-local)}))

(add-ring-handler :api/time-java (wrap-api-handler time-java-handler))

(defn bidi-test-handler [req]
  (res/response {:bidi (bidi/tag :demo/z :trott)}))

(add-ring-handler :api/bidi-test (wrap-api-handler bidi-test-handler))

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

;(import java.nio.ByteBuffer)
;(def buffer (java.nio.ByteBuffer/wrap (.getBytes "PUT Agustinho Brisolla Teste Fact\r\n")))

(defn binary-handler [req]
  ;(info "buffer: " buffer)
  (res/response
   (ring-io/piped-input-stream
    (fn [ostream]
      (spit ostream "Hello")
        ;(.write ostream buffer)
      )
      ;#(->> (io/make-writer % {})
      ;   buffer)
    )))
(add-ring-handler :api/binary
                  ;(wrap-api-handler 
                  binary-handler)
;)

