(ns webly.build
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   ;[shadow.cljs.devtools.config :as config]
   [shadow.cljs.devtools.server :as shadow-server]
   [webly.config :refer [webly-config]]))

#_(defn get-shadow-server-config []
    (let [c (-> (config/load-cljs-edn)
      ;; system config doesn't need build infos
                (dissoc :builds))]
      (info "server config: " c)
      c))

#_(defn get-build-config [build-id]
    (let [config (config/get-build! build-id)
          x (get-in config [:devtools :before-load])]
      (info "config:" config)
      (info x (type x))
      config))

#_(defn watch
    "starts a dev worker process for a given :build-id
  opts defaults to {:autobuild true}"
    ([build-id]
     (watch build-id {}))
    ([build-id opts]
     (if (worker-running? build-id)
       :already-watching
       (let [build-config
             (if (map? build-id)
               build-id
               (get-config build-id))]

         (watch* build-config opts)
         :watching))))

(defn shadow-build-config [frontend-ns]
  {:target :browser
   :output-dir "target/webly/public"
   :asset-path "/public"
   :modules {:main {:entries [frontend-ns]}}
   :devtools {:before-load (symbol "webly.web.app/before-load")
              :after-load (symbol "webly.web.app/after-load")}
   :compiler-options {:optimizations :simple}
   :build-id :demo})

(defn shadow-server-config [lein-profile handler]
  {:cache-root ".shadow-cljs"
   :lein {:profile lein-profile}
   :dev-http {8019 {:handler handler}}
   :user-config {}})

(defn build [mode lein-profile handler frontend-ns]
  (timbre/set-level! (:timbre-loglevel @webly-config))
  (info "webly " mode "handler:" handler "frontend-ns:" frontend-ns)
  (let [opts {:verbose true}
        frontend-config (shadow-build-config frontend-ns)]
    (shadow-server/start! (shadow-server-config lein-profile handler))
    (case mode
      :compile (do (shadow/compile* frontend-config opts)
                   (shadow-server/stop!))
      :watch (shadow/watch frontend-config opts))))

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