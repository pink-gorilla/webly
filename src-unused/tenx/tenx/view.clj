(ns webly.spa.tenx.view
  (:require
   [shadowx.build.prefs :refer [if-pref-fn]]))

(defn tenx-script []
  (if-pref-fn :tenx
              [:script (str "var CLOSURE_UNCOMPILED_DEFINES = {\"re_frame.trace.trace_enabled_QMARK_\":true, "
                            "\"day8.re_frame.tracing.trace_enabled_QMARK_\": true };")]
              [:div.no-tenx]))


