(ns webly.build.static
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre  :refer [debug info warn]]
   [modular.writer :refer [write write-status write-target]]
   [modular.resource.load :refer [write-resources-to]]
   [webly.spa.html.page :refer [app-page-static]]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn save-resources []
  (write-resources-to "target/static/r" "public"))

(defn generate-static-html [frontend-config]
  (let [csrf-token "llXxTmFvjm6KXhKBjY7nemz4GNRwF/ZgZbycGDgw8cdF1B/cbmX5JZElY3MCnyEYUUGCi7Cw3k3mUpMI"
        filename "target/static/index.html"]
     (info "writing static page: " filename)
  (->> (app-page-static frontend-config csrf-token)
       (spit filename))))

#_(defn config-prefix-adjust [config]
  (let [prefix (:prefix config)
        static-main-path (:static-main-path config)
        asset-path (str static-main-path prefix)]
    (info "static asset path: " asset-path)
    (assoc config :prefix asset-path)))

(defn write-static-config [opts]
  (let [filename "target/static/r/config.edn"]
    (write filename opts)))

(defn prepare-static-page [frontend-config]
  (ensure-directory "target")
  (ensure-directory "target/static")
  (ensure-directory "target/static/r")
  (write-static-config frontend-config)
  (generate-static-html frontend-config)
  (save-resources))