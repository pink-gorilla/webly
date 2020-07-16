(ns webly.build.build-config
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.cli-actual]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   [shadow.cljs.devtools.server :as shadow-server]
   [webly.build.bundle-size :refer [generate-bundlesize-report]]))

(defn generate-config [config]
  (spit "shadow-cljs.edn" (pr-str config)))

(defn watch-api []
  (let [opts {:verbose true}]
    (shadow-server/start!)
    (shadow/watch :webly opts)
                    ;(shadow-server/stop!)
    ))

(defn watch-cli []
  (shadow.cljs.devtools.cli-actual/-main "watch" "webly"))


(defn build [mode config]
  (generate-config config)
  (let [opts {:verbose true}]
    (case mode

      ; production build (onebundle file, no source-maps)
      :release (do (shadow/release :webly opts)
                   (generate-bundlesize-report))

      ; dev build (one bundle per ns, source-maps)
      :compile (do (shadow/compile :webly opts)
                   (generate-bundlesize-report))

      ; shadow-web server with build bundle (release / compile)
      :run (shadow-server/start!)

      ; hot reloading
      :watch (watch-cli)
     ; 
      )))


