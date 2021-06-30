(ns webly.config.cprop
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info warn error]]
   [cprop.core :refer [load-config]]
   [cprop.source :refer [from-env from-system-props from-resource from-file]]
   [webly.encoding.edn :as e]))

;; cprop
(defn- load-from-file [filename]
  (info "loading config from file:" filename)
  (binding [*data-readers* e/config]
    (from-file filename)))

(defn- load-from-resource [name]
  (info "loading config from resource:" name)
  (binding [*data-readers* e/config]
    (from-resource name)))

(defn- from-map-file-res [config]
  (cond
    (nil? config)
    {}

    (map? config)
    config

    (.exists (io/file config))
    (load-from-file config)

    (io/resource config)
    (load-from-resource config)

    :else
    {}))

; https://github.com/tolitius/cprop

(defn load-config-cprop [app-config]
  (let [app (if (vector? app-config)
              (into [] (map from-map-file-res app-config))
              [(from-map-file-res app-config)])
        app-creds (into [] (conj app
                                 (from-map-file-res "creds.edn")))
        ;_ (info "app creds: " app-creds)
        config-files (load-config
                      :resource "webly/config.edn" ; otherwise it would search for config.edn 
                      :merge app-creds)
        ;_ (info "cf: " config-files)
        ks (keys config-files)
        config (load-config
                :resource "webly/config.edn"
                :merge
                [config-files
                 (from-system-props)
                 (from-env) ; env otherwise has way too many settings
                 ])
        config (select-keys config ks)]
    ;(info "keys: " ks)
    ;(info "config:" config )
    ;config-files
    config))