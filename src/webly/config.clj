(ns webly.config
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info warn error]]
   [clojure.edn :as edn]
   [cprop.core :refer [load-config]]
   [cprop.source :refer [from-env from-system-props from-resource from-file]]
   [webly.encoding.edn :as e]
   [webly.log :refer [timbre-config!]]
   [webly.writer :refer [write-status]]))

(defonce config-atom (atom {}))

(defmacro get-in-config-cljs [path]
  (get-in @config-atom path))

(defn get-in-config [path]
  (get-in @config-atom path))

(defn read-config-hack [input]
  (info "reading.. hack!")
  (edn/read-string
   {:readers e/config}
   (slurp input)))

(defn load-from-file [filename]
  (info "loading webly config from file:" filename)
  ;(with-redefs [*data-readers* e/config
  ;              cprop.source/read-config read-config-hack]
  (binding [*data-readers* e/config]
    (from-file filename)))

(defn load-from-resource [name]
  (info "loading webly config from resource:" name)
  ;(with-redefs [cprop.source/read-config read-config-hack]
  (binding [*data-readers* e/config]
    (from-resource name)))

(defn from-map-file-res [config]
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

(defn- load-config-cprop [app-config]
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

(defn require-log [n]
  (info "requiring:" n)
  (require [n]))

(defn require-ns-clj []
  (let [ns-clj (get-in-config [:webly :ns-clj])]
    (if ns-clj
      (try
        (info "requiring ns-clj:" ns-clj)
        (doall
         (map require-log ns-clj))
        (catch Exception e
          (error "Exception requiring ns-clj: " (pr-str e))))
      (warn "no ns-clj defined."))))

(defn resolve-symbol [path]
  (let [s (get-in-config path)]
    (if (symbol? s)
      (try
        (info "resolving symbol: " s)
        (if-let [r (var-get (resolve s))]
          (do
            (debug "symbol " s " resolved to: " r)
            (swap! config-atom assoc-in path r)
            r)
          (do (error  "symbol in path [" path "] as: " s " could not get resolved!")
              nil))
        (catch Exception e
          (error "Exception resolving symbol in path: " path " ex:" (pr-str e))
          nil))
      s)))

(defn load-config!
  [app-config]
  (let [config (load-config-cprop app-config)]
    (reset! config-atom config)
    (timbre-config! @config-atom)
    (require-ns-clj) ; requiring ns needs to happen before resolving symbols
    (resolve-symbol [:keybindings])
    (resolve-symbol [:webly :routes])
    (write-status "config" @config-atom)
    (write-status "keybindings" (get-in @config-atom [:keybindings]))))

(defn add-config [app-config user-config]
  (let [app-config (if (vector? app-config) app-config [app-config])
        user-config (if (vector? user-config) user-config [user-config])]
    (into [] (concat app-config user-config))))


