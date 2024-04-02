(ns webly.build.core
  (:require
   [taoensso.timbre  :refer [debug info warn]]
   [modular.writer :refer [write write-status write-target]]
   [webly.build.shadow-config :refer [shadow-config]]
   [webly.build.shadow :refer [shadow-build]] ; shadow via generated config file
   [webly.build.prefs :refer [write-build-prefs]]))

(defn write-shadow-config [config]
  (let [filename "shadow-cljs.edn"]
    (write filename config)))

(defn build [exts opts profile]
  (info "webly build profile: " profile)
  (let [bundle (get profile :bundle)
        shadow-config (shadow-config exts opts profile)]
    (if bundle
      (do (info "building bundle: " bundle)
          ;(debug "shadow-config: " shadow-config) ; can be seen in generated shadow-cljs.edn
          (write-status "shadow-cljs" shadow-config)
          (write-shadow-config shadow-config)
          (write-build-prefs)
          (shadow-build profile shadow-config)
          ;(write-target "lazy" ()[name data])
          )
      (warn "profile has no bundle"))))

;(comment
  ;(get-shadow-server-config)
  ;(get-config :demo)
;  (build :watch "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
;  (build :compile "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
 ; 
;  )
