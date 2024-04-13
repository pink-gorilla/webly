(ns webly.build.static
  (:require
   [clojure.java.io :as io]
   [babashka.fs :as fs]
   [taoensso.timbre  :refer [debug info warn]]
   [modular.writer :refer [write write-status write-target]]
   [modular.resource.load :refer [write-resources-to]]
   [webly.spa.html.page :refer [app-page-static]]))

(def root "target/static/")

(def page-name "index")

(def resource-path
  "target/static/r/")

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn save-resources []
  (info "exporting resources..")
  (write-resources-to "target/static" "public")
  (fs/move "target/static/public" "target/static/r"))

(defn copy-pattern [from dest p]
  (let [files (fs/glob from p)]
    (doall (map #(fs/copy % dest {:replace-existing true}) files))))

(defn copy-js []
  (info "copying .js files..")
  (copy-pattern "target/webly/public" resource-path "*.js")
  (when (fs/exists? "target/webly/public/cljs-runtime")
    (copy-pattern "target/webly/public/cljs-runtime" (str resource-path "cljs-runtime/") "*.js")
    (copy-pattern "target/webly/public/cljs-runtime" (str resource-path "cljs-runtime/") "*.js.map")))

(defn generate-static-html [frontend-config]
  (info "generating static html page ..")
  (let [csrf-token "llXxTmFvjm6KXhKBjY7nemz4GNRwF/ZgZbycGDgw8cdF1B/cbmX5JZElY3MCnyEYUUGCi7Cw3k3mUpMI"
        filename (str root page-name ".html")]
    (info "writing static page: " filename)
    (->> (app-page-static frontend-config csrf-token)
         (spit filename))))

(defn write-static-config [opts]
  (let [filename (str resource-path "config.edn")]
    (write filename opts)))

(defn build-static [frontend-config]
  (ensure-directory "target")
  (fs/delete-tree "target/static")
  (ensure-directory "target/static")
  (save-resources)
  (write-static-config frontend-config)
  (generate-static-html frontend-config)
 ; (ensure-directory resource-path)
  (ensure-directory (str resource-path "cljs-runtime"))
  (copy-js)
;  
  )
