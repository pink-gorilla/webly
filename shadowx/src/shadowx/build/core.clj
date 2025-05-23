(ns shadowx.build.core
  (:require
   [taoensso.timbre  :refer [debug info warn]]
   [modular.writer :refer [write write-edn-private]]
   [shadowx.build.shadow-config :refer [shadow-config]]
   [shadowx.build.shadow :refer [shadow-build]] ; shadow via generated config file
   [shadowx.build.prefs :refer [write-build-prefs]]))

(defn write-shadow-config [config]
  (write "shadow-cljs.edn" config))

(defn build [exts opts profile]
  (info "webly build profile: " profile)
  (let [bundle (get profile :bundle)
        shadow-config (shadow-config exts opts profile)]
    (if bundle
      (do (info "building bundle: " bundle)
          (write-shadow-config shadow-config)
          (write-edn-private "shadow-cljs" shadow-config)

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
