(ns demo.page.main.css
  (:require
   [taoensso.timbre :refer-macros [warn]]
   [reagent.core :as r]
   [frontend.css :refer [loading? set-theme-component add-components]]
   [demo.helper.ui :refer [link-fn block2]]
   [demo.emoji :refer [emoji]]))

; CSS
(defn demo-css []
  (let [show (r/atom false)
        ; this triggers loading of the emojii css
        show! (fn []
                (warn "showing emojii ..")
                (set-theme-component :emoji true)
                (reset! show true))
        add-bad-css! (fn []
                       (warn "adding bad css ..")
                       (add-components {:available {:bad {true  ["non-existing.css"]}}
                                        :current {:bad :true}}))
        
        ]
    (fn []
      [block2 "css loader"
       [:div.flex.flex-col
        [:p "css loading? :  " (str @loading?)]
        [link-fn show! "show emoji"]
        [link-fn add-bad-css! "add non existing css"]
        (if @show
          [emoji "fiem-surprised"]
          [:span "emoji not loaded"])
        [:p "tailwind theme switcher (requires full tailwind css)"]
        [link-fn #(set-theme-component :tailwind "light") "tailwind light"]
        [link-fn #(set-theme-component :tailwind "dark") "tailwind dark"]]])))