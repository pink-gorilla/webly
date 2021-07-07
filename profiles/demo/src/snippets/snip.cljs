(ns snippets.snip
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]))

(defn add [a b]
  (info "snippet add " a b)
  (+ a b))

(defn ui-add [a b]
  [:p "addition result: " (+ a b)])
