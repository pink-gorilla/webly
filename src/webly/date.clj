(ns webly.date
  (:import java.util.Date)
  (:import java.text.SimpleDateFormat)
  (:import java.text.ParseException))

(defn now [] (java.util.Date.))

(defn tostring [dt]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") dt))

(defn now-str []
  (tostring (now)))