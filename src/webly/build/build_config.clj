(ns webly.build.build-config
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   ;[cheshire.core :as cheshire]
   [fipp.clojure]
   [shadow.cljs.devtools.cli]
   ;[shadow.cljs.devtools.cli-actual]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   [webly.build.install-npm :refer [install-npm]]
   [webly.build.bundle-size :refer [generate-bundlesize-report]]))

; fast, but no pretty-print (makes it difficult to detect bugs)
#_(defn generate-config [config]
    (spit "shadow-cljs.edn" (pr-str config)))

; only for json. keep here because we might need it for package.json writing.
#_(defn generate-config [config]
    (let [filename "shadow-cljs.edn"
          my-pretty-printer (cheshire/create-pretty-printer
                             (assoc cheshire/default-pretty-print-options
                                    :indent-arrays? true))]
      (spit filename (cheshire/generate-string config {:pretty my-pretty-printer}))))

; pretty, but slower
(defn generate-config [config]
  (let [filename "shadow-cljs.edn"
        s (with-out-str
            (fipp.clojure/pprint config {:width 40}))]
    (spit filename s)))

#_(defn watch-api
    {:shadow/requires-server true}
    []
    (let [opts {:verbose true}]
      (shadow-server/start!)
      (shadow/watch :webly opts)))

(defn watch-cli [cljs-build]
  (let [id  (name cljs-build)]
    (info "watching " id)
  ;(shadow.cljs.devtools.cli-actual/-main "watch" "webly")
    (shadow.cljs.devtools.cli/-main "watch" id)))

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






