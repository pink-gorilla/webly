(ns demo.handler.binary
  (:require

   [clojure.java.io :as io]
   [ring.util.response :as res]
   [ring.util.io :as ring-io]))


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