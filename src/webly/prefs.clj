(ns webly.prefs
  (:import java.util.Date)
  (:import java.text.SimpleDateFormat)
  (:import java.text.ParseException))

(defonce prefs-atom (atom {}))

; add compile time to prefs

(defn now [] (java.util.Date.))

(defn tostring [dt]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") dt))

(swap! prefs-atom assoc :compile-time (tostring (now)))

; macros that are used in cljs:

(defmacro pref []
  @prefs-atom)

(defmacro if-pref [kw with without]
  (list 'do
        (if (get @prefs-atom kw)
          with
          without)))

(defn if-pref-fn [kw with without]
  (if (get @prefs-atom kw)
    with
    without))