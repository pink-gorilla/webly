(ns webly.build.shadow
  "call shadow-cljs functions"
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn]]
   [shadow.cljs.devtools.server.npm-deps :as npm-deps]
   [shadow.cljs.devtools.cli]
   [shadow.cljs.devtools.api :as shadow-api]
   [shadow.cljs.devtools.server :as shadow-server]
   [shadow.cljs.build-report]
   [webly.build.static :refer [prepare-static-page]]
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
    :report-file "target/webly/public/bundlesizereport.html"})) ;".webly/bundlesizereport.html"

(defn watch-cli [cljs-build]
  (let [id  (name cljs-build)]
    (info "watching " id)
    (shadow.cljs.devtools.cli/-main "watch" id)))

(defn watch-api
  {:shadow/requires-server true}
  [cljs-build]
  (let [id  (name cljs-build)]
    (info "watching (api)" id)
    (shadow-server/start!)
    (shadow-api/watch cljs-build)
    cljs-build))

(defn stop-shadow [cljs-build]
  (warn "stopping shadow-build for: " cljs-build)
  (shadow-api/stop-worker cljs-build)
  (shadow-server/stop!))

(defn shadow-build [profile shadow-config]
  (let [{:keys [shadow-verbose cljs-build shadow-mode size-report npm-install static?]} (get profile :bundle)
        shadow-opts {:verbose shadow-verbose}]
    (ensure-package-json)
    (ensure-karma)

    (when npm-install
      (info "webly: npm install ..")
      (install-npm shadow-config shadow-opts))

    (let [s (case shadow-mode
              :release (do (info "shadow build: release")
                           (shadow-api/release cljs-build shadow-opts))  ; production build (onebundle file, no source-maps)
              :compile (do (info "shadow build: compile")
                           (shadow-api/compile cljs-build shadow-opts)) ; dev build (one bundle per ns, source-maps)
              :watch (do (info "shadow build: watch")
                 ;(watch-cli cljs-build)
                         (watch-api cljs-build)  ; hot reloading
                         )
              (debug "not building cljs bundle"))]

      (when size-report
        (info "creating size report ..")
        (generate-bundlesize-report))

      (when static?
        (info "creating static page ..")
        (prepare-static-page))

      s

     ; 
      )))
