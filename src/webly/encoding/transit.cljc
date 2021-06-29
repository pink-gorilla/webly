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



