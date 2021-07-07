(ns webly.build.shadow
  "call shadow-cljs functions"
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [shadow.cljs.devtools.server.npm-deps :as npm-deps]
   [shadow.cljs.devtools.cli]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.build-report]
   [webly.build.npm-writer :refer [ensure-package-json ensure-karma]]))

;https://github.com/thheller/shadow-cljs/blob/master/src/main/shadow/cljs/devtools/cli_actual.clj
; (let [{:keys [action builds options summary errors] :as opts}
;        (opts/parse args)
;
;        config
;        (config/load-cljs-edn!)]

(defn install-npm [config opts]
  (info "installing npm deps.." config opts)
  ;; always install since its a noop if everything is in package.json
  ;; and a server restart is not required for them to be picked up
  (npm-deps/main config opts))

(defn generate-bundlesize-report []
  (shadow.cljs.build-report/generate
   :webly
   {:print-table true
    :report-file ".webly/bundlesizereport.html"}))

(defn watch-cli [cljs-build]
  (let [id  (name cljs-build)]
    (info "watching " id)
    (shadow.cljs.devtools.cli/-main "watch" id)))

(defn shadow-build [profile shadow-config]

  (let [{:keys [shadow-verbose cljs-build shadow-mode size-report npm-install]} (get profile :bundle)
        shadow-opts {:verbose shadow-verbose}]
    (ensure-package-json)
    (ensure-karma)
    (when npm-install
      (info "webly - npm install ..")
      (install-npm shadow-config shadow-opts))

    (case shadow-mode
      :release (shadow/release cljs-build shadow-opts)  ; production build (onebundle file, no source-maps)
      :compile (shadow/compile cljs-build shadow-opts) ; dev build (one bundle per ns, source-maps)
      :watch (watch-cli cljs-build) ;(watch-api)  hot reloading
      (warn "not building cljs bundle"))

    (when size-report
      (info "creating size report ..")
      (generate-bundlesize-report))

     ; 
    ))
