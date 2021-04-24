(ns webly.user.css.view
  (:require
   [taoensso.timbre :as timbre :refer [debugf infof warn warnf errorf]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.user.css.subscriptions]))

(defn css-link-load-cb [href on-load on-error]
  [:link {:rel "stylesheet"
          :href  href
          :on-load (fn [x y]
                     (infof "css loaded: %s" href)
                     (on-load))
          :on-error (fn [x y]
                      (errorf "css load error: %s" href)
                      (on-error))}])

(defn load-css-links [css-links]
  (let [set-status (fn [s] (rf/dispatch [:webly/status-css s]))
        set-error (fn [] (set-status :error))
        total (count css-links)
        counter-loaded (r/atom 0)
        inc-counter (fn []
                      (swap! counter-loaded inc)
                      (when (>= @counter-loaded total)
                        (set-status :loaded)))]
    (fn [css-links]
      (into [:<>]
            (map-indexed (fn [i l]
                           ^{:key i}
                           [css-link-load-cb l inc-counter set-error]) css-links)))))

; https://presumably.de/reagent-mysteries-part-4-children-and-other-props.html
; children are difficult. we unload the entire app via
; re-frame suscriptions

(defn load-css []
  (let [css-links (rf/subscribe [:css/app-theme-links])]
    (fn []
      (when (not (empty? @css-links))
        (warn "css has changed to: " @css-links)
        [load-css-links @css-links]))))
