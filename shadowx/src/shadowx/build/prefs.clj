(ns shadowx.build.prefs
  (:require
   [modular.date :refer [now-str]]
   [shadowx.writer :refer [write-target2]]))

(defonce prefs-atom (atom {}))

; add compile time to prefs

(swap! prefs-atom assoc :compile-time (now-str))

; macros that are used in cljs:

(defmacro pref []
  @prefs-atom)

(defmacro get-pref [kw]
  (get @prefs-atom kw))

(defmacro if-pref [kw with without]
  (list 'do
        (if (get @prefs-atom kw)
          with
          without)))

(defn if-pref-fn [kw with without]
  (if (get @prefs-atom kw)
    with
    without))

(defn write-build-prefs []
  (write-target2 "cljsbuild-prefs" @prefs-atom))