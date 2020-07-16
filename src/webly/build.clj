(ns webly.build
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.config]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   [shadow.cljs.devtools.server.dev-http :refer [transform-server-configs]]
   [shadow.cljs.devtools.server :as shadow-server]
   [webly.build.config :refer [shadow-config]]
   [webly.build.bundle-size :refer [generate-bundlesize-report]]
   [webly.config :refer [webly-config]]))

(defn load-cljs-edn [config]
  (-> config
      (shadow.cljs.devtools.config/normalize)
      (->> (merge shadow.cljs.devtools.config/default-config))
      (update :builds #(merge shadow.cljs.devtools.config/default-builds %))
      (assoc :user-config (shadow.cljs.devtools.config/load-user-config))))

(defn run-shadow-dev-server [config]
  (info "starting dev server with config: " config)
  (with-redefs [shadow.cljs.devtools.server.dev-http/get-server-configs
                (fn []
                  (info "using server-config: " config)
                  (->  config ; (config/load-cljs-edn!)
                       (transform-server-configs)))]
    (shadow-server/start! config)))

(defn build [mode lein-profile handler frontend-ns]
  (timbre/set-level! (:timbre-loglevel @webly-config))
  (info "webly " mode "handler:" handler "frontend-ns:" frontend-ns)
  (let [opts {:verbose true}
        config (shadow-config lein-profile handler frontend-ns)]
    (spit "shadow-cljs.edn" (pr-str config))
    (with-redefs [shadow.cljs.devtools.config/load-cljs-edn #(load-cljs-edn config)]
      (run-shadow-dev-server config)
      (case mode
        :compile (do (shadow/compile* (get-in config [:builds :webly]) opts)
                     (generate-bundlesize-report config)
                     (shadow-server/stop!))
        :watch (shadow/watch (get-in config [:builds :webly]) opts)))))

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