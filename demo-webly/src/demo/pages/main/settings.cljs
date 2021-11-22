(ns demo.pages.main.settings
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.user.settings.local-storage :refer [ls-get ls-set!]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn demo-settings []
  (let [s (rf/subscribe [:settings])]
    (fn []
      [block2 "settings"
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(rf/dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(rf/dispatch [:settings/set :bongo 456]) " set bongo to 456"]
       [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]])))



