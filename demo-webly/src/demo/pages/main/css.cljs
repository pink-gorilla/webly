(ns demo.pages.main.css
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]
   [demo.emoji :refer [emoji]]))

; CSS
(defn demo-css []
  (let [show (r/atom false)
        loading? (rf/subscribe [:css/loading?])
        ; this triggers loading of the emojii css
        show! (fn []
                (warn "showing emojii")
                (rf/dispatch [:css/set-theme-component :emoji true])
                (reset! show true))]
    (fn []
      [block2 "css loader"
       [:div.flex.flex-col
        [:p "css loading?" (str @loading?)]
        [link-fn show! "show emoji"]
        [link-dispatch [:css/add-components
                        {:bad {true  ["non-existing.css"]}}
                        {:bad :true}]
         "add non existing css"]
        (if @show
          [emoji "fiem-surprised"]
          [:span "emoji not loaded"])
        [:p "tailwind theme switcher (requires full tailwind css)"]
        [link-dispatch [:css/set-theme-component :tailwind "light"] "tailwind light"]
        [link-dispatch [:css/set-theme-component :tailwind "dark"] "tailwind dark"]
        ]])))