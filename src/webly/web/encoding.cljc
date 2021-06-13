(ns webly.web.encoding
  (:require
   [webly.web.encoding-bidi :as bidi]
   [webly.web.encoding-time :as time]))


; another encoding option:
; https://nextjournal.com/schmudde/java-time


(def decode
  {:handlers
   (merge
    (:handlers time/time-deserialization-handlers)
    (:handlers bidi/bidi-deserialization-handlers))})

(def encode
  {:handlers
   (merge (:handlers time/time-serialization-handlers)
          (:handlers bidi/bidi-serialization-handlers))})



