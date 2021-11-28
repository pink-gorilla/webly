(ns webly.build.static
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info warn]]
   [modular.writer :refer [write write-status write-target]]
   [modular.resource.load :refer [write-resources-to]]
   [modular.config :refer [config-atom]]
   [webly.app.page :refer [app-page-static app-page-dynamic]]))

(defn create-html [app-page jsname html-name]
  (let [csrf-token "llXxTmFvjm6KXhKBjY7nemz4GNRwF/ZgZbycGDgw8cdF1B/cbmX5JZElY3MCnyEYUUGCi7Cw3k3mUpMI"
        html (app-page csrf-token)
        filename html-name]
    (info "writing static page: " filename)
    (spit filename html)))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn generate-static-html []
  (ensure-directory "target")
  (ensure-directory "target/static")
  (create-html app-page-static "/r/webly.js" "target/static/index.html")
  (create-html app-page-dynamic "/r/webly.js" "target/static/index_dynamic.html"))

(defn save-resources []
  (ensure-directory "target")
  (ensure-directory "target/res")
  (write-resources-to "target/res" "public"))

(defn config-prefix-adjust [config]
  (let [prefix (:prefix config)
        static-main-path (:static-main-path config)
        asset-path (str static-main-path prefix)]
    (info "static asset path: " asset-path)
    (assoc config :prefix asset-path)))

(defn write-static-config []
  (let [filename "target/static/r/config.edn"
        config (-> @config-atom
                   (dissoc :oauth2 :webly/web-server :shadow) ; oauth2 settings are private
                   (config-prefix-adjust))]
    (ensure-directory "target")
    (ensure-directory "target/static")
    (ensure-directory "target/static/r")
    (write filename config)))

(defn prepare-static-page []
  ; 1. adjust config & write
  (write-static-config)

  (generate-static-html)
  (save-resources))