(ns webly.build.lazy
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]))

(defmacro wrap-lazy [symbol-fn]
  (taoensso.timbre/info "compile-time lazy wrapping: " symbol-fn)
  `(do
     (webly.build.lazy/add-available (quote ~symbol-fn))
     (webly.build.lazy/show-lazy
      (shadow.lazy/loadable ~symbol-fn)
      (quote ~symbol-fn))))

(comment
  (macroexpand '(wrap-lazy demo.fun/add))
;
  )