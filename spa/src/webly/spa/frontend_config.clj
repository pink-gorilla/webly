(ns webly.spa.frontend-config
  (:require
   [extension :refer [discover get-extensions]]
   [webly.spa.default :as default]
   [webly.spa.service :refer [cljs-services]]))

(defn create-frontend-config [{:keys [spa prefix ports]
                               :or {spa {}
                                    prefix default/prefix}
                               :as config} exts]
  (let [spa (merge default/spa spa)
        frontend-config  {:prefix prefix
                          :spa spa
                          :cljs-services (cljs-services config exts)
                          :ports ports}]
    frontend-config))

(comment
  (def exts (discover {}))
  (get-extensions exts {:api-routes {}})

; 
  )
