(ns webly.build.config
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.config :as config]))

(defn get-shadow-server-config-edn []
  (let [c (-> (config/load-cljs-edn)
      ;; system config doesn't need build infos
              (dissoc :builds))]
    (info "edn server config: " c)
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

(defn shadow-config [lein-profile handler frontend-ns]
  {;:cache-root ".shadow-cljs"
   :lein {:profile lein-profile}
   :dev-http {9000 {;:root "public" ; shadow does not need to serve resources
                    :handler handler}}
   :http {:port 9001  ; shadow dashboard
          :host "localhost"}
   :nrepl {:port 9002
           ;:middleware [] ; optional list of namespace-qualified symbols
           }
   ;:user-config {}
   ;
   :builds {:webly {:target :browser
                    :output-dir "target/webly/public"
                    :asset-path "/r"
                    :modules {:main {:entries [frontend-ns]}}
                    ;:devtools {:before-load (symbol "webly.web.app/before-load")
                    ;           :after-load (symbol "webly.web.app/after-load")}
                    :compiler-options {:optimizations :simple}
                    ;:build-id :webly
                    }
            :ci {:target :karma
                 :output-to  "target/ci.js"
                 :ns-regexp "-test$"}}})