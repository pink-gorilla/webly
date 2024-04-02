(ns demo.page.main.keybinding
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn demo-keybinding []
  [block2 "keybindings"
   [:div.flex.flex-col
    [link-fn #(rf/dispatch [:palette/show]) "show keybindings"]]])
