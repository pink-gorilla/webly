(ns webly.util
  (:require-macros [webly.util])
  (:require
   ["react" :as react]
   [reagent.core :as r]
   [shadow.lazy :as lazy]))

;; the lazy-component macro will emit a simple

;; (demo.util/lazy-component* (shadow.lazy/loadable some.qualified/symbol))

;; you don't actually need this wrapper macro, I just wanted the code to be extra
;; short for demo purposes.

(defn lazy-component* [loadable]
  (react/lazy
   (fn []
     (-> (lazy/load loadable)
         (.then (fn [_root-el]
                   ;; React.lazy expects to load a ES6 module with a React Component as default export

                   ;; this would be more correct in production settings
                   ;; #js {:default (r/reactify-component root-el)}

                   ;; we need wrap the loaded component one extra level so live-reload actually works
                   ;; since React will keep a reference to the initially loaded fn and won't update it
                  #js {:default (r/reactify-component (fn [props] [@loadable props]))}))))))

; props has to be a map
(defn show-lazy
  ([lazy-component props]
   [:> react/Suspense {:fallback (r/as-element [:div "Loading ..."])}
    [:> lazy-component props]])
  ([lazy-component]
   [:> react/Suspense {:fallback (r/as-element [:div "Loading ..."])}
    [:> lazy-component]]))