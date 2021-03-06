(ns webly.build.core
  (:require
   [taoensso.timbre  :refer [debug info warn]]
   [webly.build.shadow-config :refer [shadow-config]]
   [webly.build.shadow :refer [shadow-build]] ; shadow via generated config file
   [webly.writer :refer [write write-status]]))

(defn write-shadow-config [config]
  (let [filename "shadow-cljs.edn"]
    (write filename config)))

(defn build [profile]
  (let [bundle (get profile :bundle)
        shadow-config (shadow-config profile)]
    (if bundle
      (do (info "building bundle: " bundle)
          ;(debug "shadow-config: " shadow-config) ; can be seen in generated shadow-cljs.edn
          (write-status "shadow-cljs" shadow-config)
          (write-shadow-config shadow-config)
          (shadow-build profile shadow-config))
      (warn "profile has no bundle"))))

;(comment
  ;(get-shadow-server-config)
  ;(get-config :demo)
;  (build :watch "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
;  (build :compile "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
 ; 
;  )