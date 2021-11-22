(ns demo.pages.main.keybinding
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn demo-keybinding []
  [block2 "keybindings"
   [:p "press [alt-g k] to see keybindings"]
   [:p "press [alt-g t] to toggle 10x"]
   [:p "press [alt-g 1] to goto main"]
   [:p "press [alt-g 2] to goto party (vienna)"]
   [:p "press [alt-g 3] to goto help"]
   [:p "press [alt-g 4] to goto job"]
   [:p "press [alt-g 5] to goto party (bali)"]])
