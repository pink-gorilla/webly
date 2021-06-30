(ns snippets.snip
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]))

(defn add [b c]
  (info "snippet add " b c)
  (+ b c))

