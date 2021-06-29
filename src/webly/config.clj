(ns webly.config
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info error]]
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

(defn res-str [str]
  (debug "resolving keybindings var: " str)
  (if-let [sym (symbol str)]
    (if-let [r (resolve sym)]
      (if-let [kb (var-get r)] kb [])
      [])
    []))

(defn resolve-config-keybindings
  "in case kb are specified as an array in config, dont replace"
  [config]
  (let [str (:keybindings config)]
    (if (and (not (nil? str))
             (string? str))
      (if-let [kb (res-str str)]
        (do ;(debug "keybindings resolved to: " kb)
          (write-status "keybindings" kb)
          (assoc config :keybindings kb))
        config)
      config)))

(defn load-config!
  [app-config]
  (let [config (load-config-cprop app-config)
        config (resolve-config-keybindings config)]
    (reset! config-atom config)
    (write-status "config" @config-atom)
    (timbre-config! @config-atom)))

(defn add-config [app-config user-config]
  (let [app-config (if (vector? app-config) app-config [app-config])
        user-config (if (vector? user-config) user-config [user-config])]
    (into [] (concat app-config user-config))))


