(ns webly.build.install-npm
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [shadow.cljs.devtools.server.npm-deps :as npm-deps]))

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


