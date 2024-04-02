(ns webly.build.shadow-config
  "generates shadow-cljs.edn based on profile and config"
  (:require
   [webly.spa.default :as default]
   [modular.writer :refer [write-target]]
   [webly.build.prefs :refer [if-pref-fn prefs-atom]]
   [extension :refer [write-service]]
   [webly.module.build :refer [create-modules shadow-module-config]]))

;; build-options
(defn build-ns-aliases []
  (if-pref-fn :tenx
              {'webly.app.tenx.events 'webly.app.tenx.events-on}
              {'webly.app.tenx.events 'webly.app.tenx.events-off
               ;'day8.re-frame.tracing 'day8.re-frame.tracing-stubs
               }))

;; shadow config
(defn shadow-config [exts
                     {:keys [build shadow spa prefix static-main-path]
                      :or {build default/build
                           shadow default/shadow
                           spa default/spa
                           prefix default/prefix
                           static-main-path ""}
                      :as config} profile]
  (let [;; PROFILE *************************************************
        advanced? (get-in profile [:bundle :advanced])
        shadow-verbose (get-in profile [:bundle :shadow-verbose])
        static? (get-in profile [:bundle :static?])
        ;; CONFIG **************************************************
        build (merge default/build build) ; in case user just specified some keys
        spa (merge default/spa spa)
        shadow (merge default/shadow shadow)
        {:keys [module-loader-init output-dir]} build
        {:keys [start-user-app]} spa
        dev-http-port (get-in shadow [:dev-http :port])
        http-port (get-in shadow [:http :port])
        http-host (get-in shadow [:http :host])
        nrepl-port (get-in shadow [:nrepl :port])
        main-path (if static? static-main-path "")
        prefix (str main-path prefix)
        asset-path  (subs prefix 0 (dec (count prefix))) ;  "/r/" => "/r"
        modules (create-modules exts)
        shadow-modules (shadow-module-config modules)]
    (swap! prefs-atom assoc
           :main-path main-path
           :asset-path asset-path
           :advanced? advanced?
           :start-user-app start-user-app)
    (write-service exts :shadow-modules shadow-modules)
    {:cache-root ".shadow-cljs"
     :verbose (if shadow-verbose true false)
     :lein false
     :dev-http {dev-http-port {;:root "public" ; shadow does not need to serve resources
                               :handler (-> shadow :ring-handler symbol)}}
     :http {:port http-port  ; shadow dashboard
            :host http-host}
     :nrepl {:port nrepl-port
           ;:middleware [] ; optional list of namespace-qualified symbols
             }

   ;:user-config {}
   ;
     :builds {:webly {:target :browser
                      :module-loader true
                      :module-loader-init module-loader-init ; bool, true = auto-init, false = manual-init
                      :output-dir output-dir
                      :asset-path asset-path
                      :modules shadow-modules
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

