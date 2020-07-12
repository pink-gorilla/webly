(ns webly.shadow.build
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   ;[shadow.cljs.devtools.config :as config]
   [shadow.cljs.devtools.server :as shadow-server]))

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

(def shadow-build-config
  {:target :browser
   :output-dir "target/demo/public"
   :asset-path "/public"
   :modules {:main {:entries [(symbol "demo.app")]}}
   :devtools {:before-load (symbol "demo.app/stop")
              :after-load (symbol "demo.app/start")}
   :compiler-options {:optimizations :simple}
   :build-id :demo})

(def shadow-server-config
  {:cache-root ".shadow-cljs"
   :lein {:profile "+dev"}
   :dev-http {8019 {:handler (symbol "demo.app/handler")}}
   :user-config {}})

(defn build [mode]
  (info "webly build mode: " mode)
  (let [opts {:verbose true}]
    (shadow-server/start! shadow-server-config)
    (case mode
      :compile (do (shadow/compile* shadow-build-config opts)
                   (shadow-server/stop!))
      :watch (shadow/watch shadow-build-config opts))))

(comment

  ;(get-shadow-server-config)
  ;(get-config :demo)
  (build :watch)
  (build :compile)
  (info (+ 7 7))
 ; 
  )