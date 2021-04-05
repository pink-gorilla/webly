(ns webly.config
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [info]]
   [cprop.core :refer [load-config]]
   [cprop.source :refer [from-env from-system-props from-resource from-file]]))

(def config-atom (atom {}))

(defn get-in-config [path]
  (get-in @config-atom path))

(defn from-file-exists [filename]
  (if (.exists (io/file filename))
    (from-file filename)
    {}))

(defn from-resource-exists [name]
  (let [r (io/resource name)]
    (if r
      (from-resource name)
      {})))

; https://github.com/tolitius/cprop
(defn- load-config-cprop []
  (load-config
   :resource "webly/config.edn" ; otherwise it would search for config.edn 
   :merge
   [;(from-resource "webly/config.edn")
    (from-file-exists "config.edn")
    (from-file-exists "creds.edn")
    (from-resource-exists "creds.edn")
    ;(from-system-props)
    ;(from-env)
      ;(cli-args) 
    ]))

(defn load-config! []
  (let [config (load-config-cprop)]
    (info "webly-config: " config)
    (reset! config-atom config)))

(comment

  (load-config)
  (load-config!)
  ;
  )

