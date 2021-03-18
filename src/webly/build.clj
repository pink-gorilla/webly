(ns webly.build
  (:require
   [taoensso.timbre  :refer [info]]
   [webly.build.config :refer [shadow-config]]
   [webly.config :refer [webly-config timbre-config!]]
   [webly.build.build-config] ; shadow via generated config file
   ))

(defn build [mode lein-profile handler frontend-ns]
  (timbre-config!)
  (info "webly " mode "handler:" handler "frontend-ns:" frontend-ns)
  (let [config (shadow-config lein-profile handler frontend-ns)]
    (info "shadow-config: " config)
    (webly.build.build-config/build mode config)))

(defn build-cli
  [mode lein-profile handler frontend-ns]
  (let [mode (keyword mode)
        handler (symbol handler)
        frontend-ns (symbol frontend-ns)]
    (build mode lein-profile handler frontend-ns)))

;(comment
  ;(get-shadow-server-config)
  ;(get-config :demo)
;  (build :watch "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
;  (build :compile "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
 ; 
;  )