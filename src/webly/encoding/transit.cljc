(ns webly.encoding.transit
  (:require
   [webly.encoding.bidi :as bidi]
   [webly.encoding.time :as time]))

; another encoding option:
; https://nextjournal.com/schmudde/java-time

; transit encoding is used in
; - ring middleware (muuntaja)
; - websocket (sente packer)
; - cljs ajax requests ()

(def decode
  {:handlers
   (merge
    (:handlers time/time-deserialization-handlers)
    (:handlers bidi/bidi-deserialization-handlers))})

(def encode
  {:handlers
   (merge (:handlers time/time-serialization-handlers)
          (:handlers bidi/bidi-serialization-handlers))})

; todo: 
; (def ^:private default-readers {'ig/ref ref, 'ig/refset refset})
; (defn read-string "Read a config from a string of edn. Refs may be denotied by tagging keywords with #ig/ref." ([s] (read-string {:eof nil} s)) ([opts s] (let [readers (merge default-readers (:readers opts {}))] (edn/read-string (assoc opts :readers readers) s))))


