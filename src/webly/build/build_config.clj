(ns webly.build.build-config
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.cli]
   ;[shadow.cljs.devtools.cli-actual]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]

   [webly.build.install-npm :refer [install-npm]]
   [webly.build.bundle-size :refer [generate-bundlesize-report]]))

(defn generate-config [config]
  (spit "shadow-cljs.edn" (pr-str config)))

#_(defn watch-api
    {:shadow/requires-server true}
    []
    (let [opts {:verbose true}]
      (shadow-server/start!)
      (shadow/watch :webly opts)))

(defn watch-cli [cljs-build]
  ;(shadow.cljs.devtools.cli-actual/-main "watch" "webly")
  (shadow.cljs.devtools.cli/-main "watch" (name cljs-build)))

(defn build [profile shadow-config]
  (generate-config shadow-config)
  (let [{:keys [shadow-verbose cljs-build shadow-mode size-report]} (get profile :bundle)
        shadow-opts {:verbose shadow-verbose}]

    (install-npm shadow-config shadow-opts)

    (case shadow-mode
      :release (shadow/release cljs-build shadow-opts)  ; production build (onebundle file, no source-maps)
      :compile (shadow/compile cljs-build shadow-opts) ; dev build (one bundle per ns, source-maps)
      :watch (watch-cli cljs-build) ;(watch-api)  hot reloading
      )

    (when size-report
      (info "creating size report ..")
      (generate-bundlesize-report))

     ; 
    ))






