(ns webly.build.shadow-config
  "generates shadow-cljs.edn based on profile and config"
  (:require
   [taoensso.timbre :as timbre :refer [debug info]]
   [clojure.string :as str]
   [modular.config :refer [get-in-config]]
   [modular.writer :refer [write-target]]
   [webly.build.prefs :refer [if-pref-fn prefs-atom]]))

;; build-options
(defn build-ns-aliases []
  (if-pref-fn :tenx
              {'webly.app.tenx.events 'webly.app.tenx.events-on}
              {'webly.app.tenx.events 'webly.app.tenx.events-off
               ;'day8.re-frame.tracing 'day8.re-frame.tracing-stubs
               }))

;; modules
(defn main-config-dynamic [ns-cljs]
  (let [ns-cljs (or ns-cljs [])]
    (into []
          (concat '[webly.app.app] ns-cljs))))

(defn sub-module-config [[name ns-mod]]
  (let [ns-mod (or ns-mod {})]
    {name {:entries ns-mod
           :depends-on #{:webly}}}))

(defn module-config [ns-cljs modules]
  (let [main {:webly  {:entries (main-config-dynamic ns-cljs)}
              ;:webly_dynamic {:init-fn 'webly.app.dynamic/start
              ;                :depends-on #{:webly_shared}
              ;                :entries '[webly.app.dynamic]}
              ;:webly_static {:init-fn 'webly.app.static/start
              ;               :depends-on #{:webly_shared}
              ;               :entries '[webly.app.static]}
              }
        sub (map sub-module-config modules)
        subs (apply merge sub)]
    (merge main subs)))

;; shadow config
(defn shadow-config [profile]
  (let [;; PROFILE *************************************************
        advanced? (get-in profile [:bundle :advanced])
        shadow-verbose (get-in profile [:bundle :shadow-verbose])
        static? (get-in profile [:bundle :static?])
        ;; CONFIG **************************************************
        {:keys [ns-cljs ring-handler modules title start-user-app]
         :or {modules {}}} (get-in-config [:webly])
        ring-handler (symbol ring-handler)
        dev-http-port (get-in-config [:shadow :dev-http :port])
        http-port (get-in-config [:shadow :http :port])
        http-host (get-in-config [:shadow :http :host])
        nrepl-port (get-in-config [:shadow :nrepl :port])
        main-path (if static?
                    (get-in-config [:static-main-path])
                    "")
        prefix (str main-path
                    (get-in-config [:prefix]))
        asset-path  (subs prefix 0 (dec (count prefix))) ;  "/r/" => "/r"
        ]
    (swap! prefs-atom assoc
           :main-path main-path
           :asset-path asset-path
           :advanced? advanced?
           :start-user-app start-user-app)
    (write-target "build-config" {:ns-cljs (main-config-dynamic ns-cljs)
                                  :modules modules})
    {:cache-root ".shadow-cljs"
     :verbose (if shadow-verbose true false)
     :lein false
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
                      :asset-path asset-path
                      :modules (module-config ns-cljs modules)
                    ;:devtools {:before-load (symbol "webly.web.app/before-load")
                    ;           :after-load (symbol "webly.web.app/after-load")}
                      :build-options    {:ns-aliases (build-ns-aliases)}
                      :compiler-options {:optimizations (if advanced?
                                                          :advanced
                                                          :simple)
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

