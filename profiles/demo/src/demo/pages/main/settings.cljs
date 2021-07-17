(ns demo.pages.main.settings
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.user.settings.local-storage :refer [ls-get ls-set!]]
   [webly.build.lazy :refer-macros [wrap-lazy] :refer [available]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn lazy1 []
  (let [ui-add (wrap-lazy snippets.snip/ui-add)]
    [:div
     [ui-add 7 7]]))

(defn lazy2 []
  (let [ui-add-more (wrap-lazy snippets.snip/ui-add-more)]
    [:div
     [ui-add-more 7 7]]))

(defn demo-settings []
  (let [show-lazy1 (r/atom false)
        show-lazy2 (r/atom false)
        s (rf/subscribe [:settings])]
    (fn []
      [block2 "settings"
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(rf/dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(rf/dispatch [:settings/set :bongo 456]) " set bongo to 456"]
       [link-fn #(reset! show-lazy1 true) "lazy load1"]
       [link-fn #(reset! show-lazy2 true) "lazy load2 (not working)"]
       [:div "lazy renderer: " (pr-str (available))]
       (when @show-lazy1
         [lazy1])
       (when @show-lazy2
         [lazy2])

       [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]])))


