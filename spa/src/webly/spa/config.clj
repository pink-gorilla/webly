(ns webly.spa.config
  (:require
   [extension :refer [discover get-extensions]]
   ;[frontend.css :refer [get-theme-config]]
   [webly.spa.default :as default]
   [webly.spa.service :refer [cljs-services]]))

;; Extension config

(defn- get-api-routes [exts]
  (->> (get-extensions exts {:api-routes {}})
       (map :api-routes)
       (apply merge)))

; duplicate of bidi2 
(defn get-cljs-routes [exts]
  (->> (get-extensions exts {:cljs-routes {}})
       (map :cljs-routes)
       (apply merge)))

(defn- get-routes [exts]
  {:api (get-api-routes exts)
   :app (get-cljs-routes exts)})

(defn configure [{:keys [spa prefix ports]
                  :or {spa {} 
                       prefix default/prefix}
                  :as config} exts]
  (let [routes (get-routes exts)
        spa (merge default/spa spa)
        frontend-config  {:prefix prefix
                          :spa spa
                          ;:theme theme
                          :cljs-services (cljs-services config exts)
                          :ports ports
                          }]
    {:routes routes
     :frontend-config frontend-config}))

(comment
  (def exts (discover {}))
  (get-extensions exts {:api-routes {}})
  (get-api-routes exts)
  (get-cljs-routes exts)
  (get-routes exts)

; 
  )
