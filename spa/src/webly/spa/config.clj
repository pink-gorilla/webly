(ns webly.spa.config
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [extension :refer [discover write-service get-extensions]]
   [webly.spa.default :as default]))

 ;; Extension config

(defn- get-api-routes [exts]
  (->> (get-extensions exts {:api-routes {}})
       (map :api-routes)
       (apply merge)))

(defn- get-cljs-routes [exts]
  (->> (get-extensions exts {:cljs-routes {}})
       (map :cljs-routes)
       (apply merge)))

(defn- get-routes [exts]
  {:api (get-api-routes exts)
   :app (get-cljs-routes exts)})

(defn- get-theme [exts]
  (let [themes (->> (get-extensions exts {:theme {:available {} :current {}}})
                    (map :theme))]
    {:available (reduce merge {} (map :available themes))
     :current (reduce merge {} (map :current themes))}))

(defn configure [{:keys [spa google-analytics prefix keybindings settings]
                  :or {spa {}
                       keybindings default/keybindings
                       settings default/settings
                       google-analytics default/google-analytics
                       prefix default/prefix}
                  :as config} exts]
  (let [timbre-cljs (or (:timbre/cljs config) default/timbre-cljs)
        routes (get-routes exts)
        theme (get-theme exts)
        spa (merge default/spa spa)
        frontend-config  {:prefix prefix
                          :spa spa
                          :frontend-routes (:app routes)
                          :theme theme
                          :keybindings keybindings
                          :settings settings
                          :timbre/cljs timbre-cljs
                          :google-analytics google-analytics}]
    {:routes routes
     :frontend-config frontend-config}))

(comment
  (def exts (discover {}))
  (get-extensions exts {:api-routes {}})
  (get-api-routes exts)
  (get-cljs-routes exts)
  (get-routes exts)
  (get-theme exts)

; 
  )
