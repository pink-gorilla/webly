(ns webly.date
  (:import
   [java.util Date]
   [java.text SimpleDateFormat])) ; ParseException

(defn now [] (Date.))

(defn tostring [dt]
  (.format (SimpleDateFormat. "yyyy-MM-dd HH:mm:ss") dt))

(defn now-str []
  (tostring (now)))