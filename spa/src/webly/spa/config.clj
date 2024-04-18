(ns webly.spa.config
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [extension :refer [discover write-service get-extensions]]
   [webly.spa.default :as default]
   [frontend.css :as theme]
   [webly.spa.service :refer [cljs-services]]))

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

(defn configure [{:keys [spa google-analytics prefix settings]
                  :or {spa {}
                       settings default/settings
                       google-analytics default/google-analytics
                       prefix default/prefix}
                  :as config} exts]
  (let [routes (get-routes exts)
        theme (theme/get-theme-config exts)
        spa (merge default/spa spa)
        frontend-config  {:prefix prefix
                          :spa spa
                          :frontend-routes (:app routes)
                          :theme theme
                          :settings settings
                          :cljs-services (cljs-services config exts)
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
