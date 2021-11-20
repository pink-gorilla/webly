(ns webly.user.css.view
  (:require
   [taoensso.timbre :as timbre :refer [debugf info infof warn warnf errorf]]
   [re-frame.core :as rf]
   [webly.user.css.dom :refer [existing-css update-css]]
   [webly.user.css.subscriptions]))

; css loading status not implemented correctly.
(rf/dispatch [:webly/status-css :loaded])
; (rf/dispatch [:webly/status-css :error])

; https://presumably.de/reagent-mysteries-part-4-children-and-other-props.html
; children are difficult. we unload the entire app via
; re-frame suscriptions

(defn load-css []
  ;(info "existing links: " (existing-css))
  (let [configured? (rf/subscribe [:webly/status-of :configured?])
        css-links (rf/subscribe [:css/app-theme-links])]
    (fn []
      (when (and @configured?
                 (not (empty? @css-links)))
        ;(info "css has changed to: " @css-links)
        (update-css @css-links))
      [:div.webly-css-loader])))
