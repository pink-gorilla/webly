(ns webly.build
  (:require
   [taoensso.timbre  :refer [info]]
   [webly.build.config :refer [shadow-config]]
   [webly.config :refer [load-config! get-in-config]]
   [webly.log :refer [timbre-config!]]
   [webly.build.build-config] ; shadow via generated config file
   ))

(defn build [mode]
  (load-config!)
  (timbre-config! (get-in-config [:timbre-loglevel]))
  (info "webly build: " mode)
  (let [config (shadow-config)]
    (info "shadow-config: " config)
    (webly.build.build-config/build mode config)))

(defn build-cli
  [mode]
  (let [mode (keyword mode)]
    (build mode)))

;(comment
  ;(get-shadow-server-config)
  ;(get-config :demo)
;  (build :watch "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
;  (build :compile "+dev" (symbol "demo.app/handler") (symbol "demo.app"))
 ; 
;  )