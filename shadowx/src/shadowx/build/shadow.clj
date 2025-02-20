(ns shadowx.build.shadow
  "call shadow-cljs functions"
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]
   [shadow.cljs.devtools.server.npm-deps :as npm-deps]
   [shadow.cljs.devtools.cli]
   [shadow.cljs.devtools.api :as shadow-api]
   [shadow.cljs.devtools.server :as shadow-server]
   [shadow.cljs.build-report]
   [shadowx.build.npm-writer :refer [ensure-package-json ensure-karma]]))

;https://github.com/thheller/shadow-cljs/blob/master/src/main/shadow/cljs/devtools/cli_actual.clj
; (let [{:keys [action builds options summary errors] :as opts}
;        (opts/parse args)
;
;        config
;        (config/load-cljs-edn!)]

(defn install-npm [config opts]
  (info "installing npm deps .. ")
  ;; always install since its a noop if everything is in package.json
  ;; and a server restart is not required for them to be picked up
  (npm-deps/main config opts))

(defn generate-bundlesize-report [_profile shadow-config]
  ;(println "generate-bundlesize-report profile:" profile " shadow-config: " shadow-config)
  (let [build-data (-> shadow-config :builds :webly)]
    ;(println "generate-bundlesize-report build-data: " build-data)
    (shadow.cljs.build-report/generate
     (merge build-data {:build-id :webly}) ; previously this was only the build-id as a keyword
     {; tag
    ; data-file
      :print-table true
      :report-file ".gorilla/public/bundlesizereport.html"}))) ;".webly/bundlesizereport.html"

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
  (let [full-profile  (get profile :bundle)
        {:keys [shadow-verbose cljs-build shadow-mode size-report npm-install _static?]} full-profile
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
              nil)]

      (when size-report
        (info "creating size report ..")
        (generate-bundlesize-report profile shadow-config))
      s)))
