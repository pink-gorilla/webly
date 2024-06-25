(ns demo.page.main.keybinding
  (:require
   [re-frame.core :as rf]
   [demo.helper.ui :refer [link-fn block2]]))

(defn demo-keybinding []
  [block2 "keybindings"
   [:div.flex.flex-col
    [link-fn #(rf/dispatch [:palette/show]) "show keybindings"]]])
