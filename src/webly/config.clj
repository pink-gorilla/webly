(ns webly.config
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info error]]
   [cprop.core :refer [load-config]]
   [cprop.source :refer [from-env from-system-props from-resource from-file]]))

(defonce config-atom (atom {}))

(defmacro get-in-config-cljs [path]
  (get-in @config-atom path))

(defn get-in-config [path]
  (get-in @config-atom path))

(defn from-file-exists [filename]
  (if (.exists (io/file filename))
    (do (info "loading webly config from file: " filename)
        (from-file filename))
    {}))

(defn from-resource-exists [name]
  (let [r (io/resource name)]
    (if r
      (do (info "loading webly config from resource " name)
          (from-resource name))
      {})))

; https://github.com/tolitius/cprop
(defn- load-config-cprop [user-config]
  (let [config-files (load-config
                      :resource "webly/config.edn" ; otherwise it would search for config.edn 
                      :merge
                      [(from-file-exists "config.edn")   ; user config/creds (files)
                       (from-file-exists "creds.edn")
                       (from-resource-exists "config.edn")  ; user config/creds (resources)
                       (from-resource-exists "creds.edn")
                       (or user-config {}) ; used in lein-pinkgorilla
                       ])
        ks (keys config-files)
        config (load-config
                :merge
                [config-files
                 (from-system-props)
                 (from-env) ; env otherwise has way too many settings
                ;(cli-args) 
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
        (do (info "keybindings resolved to: " kb)
            (assoc config :keybindings kb))
        config)
      config)))

(defn load-config!
  ([]
   (load-config! {}))
  ([user-config]
   (let [config (load-config-cprop user-config)
         _ (info "webly-config: " config)
         config (resolve-config-keybindings config)]

     (reset! config-atom config))))

(comment

  (load-config)
  (load-config!)
  ;
  )

