(ns src-unused.shadow)

 ;[shadow.cljs.devtools.cli-actual]
 ;:refer [watch* worker-running?]

 ;(shadow.cljs.devtools.cli-actual/-main "watch" "webly")

[shadow.cljs.devtools.config :as config]

(defn get-shadow-server-config-edn []
  (let [c (-> (config/load-cljs-edn)
      ;; system config doesn't need build infos
              (dissoc :builds))]
    (info "edn server config: " c)
    c))


#_(defn watch-api
    {:shadow/requires-server true}
    []
    (let [opts {:verbose true}]
      (shadow-server/start!)
      (shadow/watch :webly opts)))

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