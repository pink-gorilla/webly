(ns webly.app.config
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



(defn configure [{:keys [spa google-analytics prefix]
                  :or {spa {}
                       google-analytics default/google-analytics
                       prefix default/prefix}
                  :as config} exts]
  (let [routes (get-routes exts)
        theme (get-theme exts)
        spa (merge default/spa spa)
        user-config  (select-keys config
                                  [; application specific keys
                                   :settings ; localstorage
                                   :keybindings
                                   :timbre/cljs])
        frontend-config (merge user-config {:prefix prefix
                                            :spa spa
                                            :frontend-routes (:app routes)
                                            :theme theme
                                            :google-analytics google-analytics})]
    {:routes routes
     :frontend-config frontend-config}
    ))

 