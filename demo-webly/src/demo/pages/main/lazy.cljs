(ns demo.pages.main.lazy
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.build.lazy :refer-macros [wrap-lazy] :refer [available]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

(defn lazy1 []
  (let [ui-add (wrap-lazy snippets.snip/ui-add)]
    [:div.bg-red-500
     [:p "I am lazy:"]
     [ui-add 7 7]]))

(defn lazy2 []
  (let [ui-add-more (wrap-lazy snippets.snip/ui-add-more)]
    [:div
     [ui-add-more 7 7]]))


(defn demo-lazy []
  (let [show-lazy1 (r/atom false)
        show-lazy2 (r/atom false)]
    (fn []
      [block2 "lazy ui"
       [link-fn #(reset! show-lazy1 true) "lazy load1"]
       [link-fn #(reset! show-lazy2 true) "lazy load2 (not working)"]
       [:div "loaded lazy renderer: " (pr-str (available))]
       (when @show-lazy1
         [lazy1])
       (when @show-lazy2
         [lazy2])
       ])))

