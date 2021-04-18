(ns webly.build.config
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.config :as config]
   [webly.config :refer [get-in-config]]
   [webly.prefs :refer [if-pref-fn prefs-atom]]))

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

(defn build-ns-aliases []
  (println @prefs-atom)
  (if-pref-fn :tenx
              {'webly.user.tenx.events 'webly.user.tenx.events-on}
              {'webly.user.tenx.events 'webly.user.tenx.events-off
               ;'day8.re-frame.tracing 'day8.re-frame.tracing-stubs
               }))

(defn shadow-config []
  (let [{:keys [lein-cljs-profile ns-cljs-app ring-handler]} (get-in-config [:webly])
        ring-handler (symbol ring-handler)
        dev-http-port (get-in-config [:shadow :dev-http :port])
        http-port (get-in-config [:shadow :http :port])
        http-host (get-in-config [:shadow :http :host])
        nrepl-port (get-in-config [:shadow :nrepl :port])]
    {:cache-root ".shadow-cljs"
     :verbose true
     ;:lein true
     :lein {:profile lein-cljs-profile}
     :dev-http {dev-http-port {;:root "public" ; shadow does not need to serve resources
                               :handler ring-handler}}
     :http {:port http-port  ; shadow dashboard
            :host http-host}
     :nrepl {:port nrepl-port
           ;:middleware [] ; optional list of namespace-qualified symbols
             }
   ;:user-config {}
   ;
     :builds {:webly {:target :browser
                      :module-loader true
                      :output-dir "target/webly/public"
                      :asset-path "/r"
                      :modules {:main     {:entries ns-cljs-app}
                                :snippets {:entries ['snippets.snip]
                                           :depends-on #{:main}}
                                :docs {:entries ['webly.user.markdown.views]
                                       :depends-on #{:main}}}
                    ;:devtools {:before-load (symbol "webly.web.app/before-load")
                    ;           :after-load (symbol "webly.web.app/after-load")}
                      :build-options    {:ns-aliases (build-ns-aliases)}
                      :compiler-options {:optimizations :simple
                                        ; :optimizations   :none ;; Beware: releasing :none not supported by shadow
                                         ;:pretty-print true
                                        ;:keep-native-requires true
                                         ;:closure-defines {re-highlight-demo.config/version "lein-git-inject/version"}
                                         :output-feature-set :es8 ; this should fix vega polyfill problems
                                         }
                    ;:build-id :webly
                    ;:js-options  {:minimize-require false ; module requires full name instead of index
                    ;              ;:js-package-dirs ["packages/babel-worker/node_modules"]
                    ;              ;:js-provider :require
                      ;             :js-provider :closure
                      ;            :js-provider :shadow                                            
                    ;                     }
                      }
              :ci     {:target :karma
                       :output-to  "target/ci.js"
                       :ns-regexp "-test$"}}}))