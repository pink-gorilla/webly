(ns webly.web.encoding-time
  (:require
   [cognitect.transit :as transit]
   #?@(:cljs
       [[cljs-time.core :as t]
        [cljs-time.format :as tf]
        goog.date.Date
        goog.date.DateTime
        goog.date.UtcDateTime]))
  #?(:clj
     (:import [java.time
               LocalTime
               LocalDate
               LocalDateTime
               ZonedDateTime]
              [java.time.format
               DateTimeFormatter])))

;; https://github.com/luminus-framework/luminus-transit/blob/master/src/luminus_transit/time.cljc

(def iso-local-time
  #?(:clj (DateTimeFormatter/ofPattern "HH:mm:ss.SSS"))
  #?(:cljs (tf/formatter "HH:mm:ss.SSS")))

(def iso-local-date
  #?(:clj (DateTimeFormatter/ofPattern "yyyy-MM-dd"))
  #?(:cljs (tf/formatter "yyyy-MM-dd")))

(def iso-local-date-time
  #?(:clj (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSS"))
  #?(:cljs (tf/formatter "yyyy-MM-dd'T'HH:mm:ss.SSS")))

(def iso-zoned-date-time
  #?(:clj (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSSXX"))
  #?(:cljs (tf/formatter "yyyy-MM-dd'T'HH:mm:ss.SSSZ")))

#?(:cljs
   (def time-deserialization-handlers
     {:handlers
      {"LocalTime"     (transit/read-handler #(tf/parse iso-local-time %))
       "LocalDate"     (transit/read-handler #(tf/parse iso-local-date %))
       "LocalDateTime" (transit/read-handler #(tf/parse iso-local-date-time %))
       "ZonedDateTime" (transit/read-handler #(tf/parse iso-zoned-date-time %))}}))

#?(:cljs
   (def time-serialization-handlers
     {:handlers
      {goog.date.Date        (transit/write-handler
                              (constantly "LocalDate")
                              #(tf/unparse iso-local-date %))
       goog.date.DateTime    (transit/write-handler
                              (constantly "LocalDateTime")
                              #(tf/unparse iso-local-date-time %))
       goog.date.UtcDateTime (transit/write-handler
                              (constantly "ZonedDateTime")
                              #(tf/unparse iso-zoned-date-time %))}}))

#?(:clj
   (def time-deserialization-handlers
     {:handlers
      {"LocalTime"     (transit/read-handler #(java.time.LocalDate/parse % iso-local-time))
       "LocalDate"     (transit/read-handler #(java.time.LocalDate/parse % iso-local-date))
       "LocalDateTime" (transit/read-handler #(java.time.LocalDateTime/parse % iso-local-date-time))
       "ZonedDateTime" (transit/read-handler #(java.time.LocalDate/parse % iso-zoned-date-time))}}))

#?(:clj
   (def time-serialization-handlers
     {:handlers
      {java.time.LocalTime     (transit/write-handler
                                (constantly "LocalTime")
                                #(.format % iso-local-time))
       java.time.LocalDate     (transit/write-handler
                                (constantly "LocalDate")
                                #(.format % iso-local-date))
       java.time.LocalDateTime (transit/write-handler
                                (constantly "LocalDateTime")
                                #(.format % iso-local-date-time))
       java.time.ZonedDateTime (transit/write-handler
                                (constantly "ZonedDateTime")
                                #(.format % iso-zoned-date-time))}}))