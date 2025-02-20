(ns shadowx.build.lazy
  (:require
   [taoensso.timbre :refer-macros [debug]]))

(defmacro wrap-lazy [symbol-fn]
  (taoensso.timbre/debug "compile-time lazy wrapping: " symbol-fn)
  `(do
     (shadowx.build.lazy/add-available (quote ~symbol-fn))
     (shadowx.build.lazy/show-lazy
      (shadow.lazy/loadable ~symbol-fn)
      (quote ~symbol-fn))))

(comment
  (macroexpand '(wrap-lazy demo.fun/add))
;
  )