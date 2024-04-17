(ns snippets.snip
  (:require
   [reagent.core :as r]
   [taoensso.timbre :refer-macros [debug info warn error]]))

(defn add [a b]
  (info "snippet add " a b)
  (+ a b))

(defn ui-add [a b]
  [:p "addition result: " (+ a b)])

(defn ui-add-more-impl [a b]
  (let [x (r/atom 0)]
    (fn [a b]
      (swap! x inc)
      [:p "addition result: " (+ a b @x)])))

(defn ui-add-more [a b]
  [ui-add-more-impl a b])