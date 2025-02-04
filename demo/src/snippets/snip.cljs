(ns snippets.snip
  (:require
   [taoensso.timbre :refer-macros [info]]))

(defn add [a b]
  (info "snippet add " a b)
  (+ a b))

(defn ui-add [a b]
  [:p "addition result: " (str (+ a b))])