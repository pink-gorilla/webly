(ns webly.build.core
  (:require
   [taoensso.timbre  :refer [info warn]]
   [webly.build.config :refer [shadow-config]]
   [webly.build.build-config] ; shadow via generated config file
   ))

(defn build [profile]
  (let [bundle (get profile :bundle)
        shadow-config (shadow-config)]
    (if bundle
      (do (info "building profile bundle" bundle)
          (info "shadow-config: " shadow-config)
          (webly.build.build-config/build profile shadow-config))
      (warn "profile has no bundle"))))



;(comment
  ;(get-shadow-server-config)
  ;(get-config :demo)
;  (build :watch "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
;  (build :compile "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
 ; 
;  )