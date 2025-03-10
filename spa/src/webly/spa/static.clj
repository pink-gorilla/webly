(ns webly.spa.static
  (:require
   [clojure.java.io :as io]
   [babashka.fs :as fs]
   [taoensso.timbre  :refer [info]]
   [modular.writer :refer [write]]
   [modular.resource.load :refer [write-resources-to]]
   [webly.spa.html.page :refer [app-page-static]]))

(def root ".gorilla/static/")

(def page-name "index")

(def resource-path
  ".gorilla/static/r/")

(defn- ensure-directory [path]
  (fs/create-dirs path))

(defn save-resources []
  (info "exporting resources..")
  (write-resources-to ".gorilla/static" "public")
  (fs/move ".gorilla/static/public" ".gorilla/static/r"))

(defn copy-pattern [from dest p]
  (let [files (fs/glob from p)]
    (doall (map #(fs/copy % dest {:replace-existing true}) files))))

(defn copy-js []
  (when (fs/exists? ".gorilla/public")
    (info "copying .js files..")
    (copy-pattern ".gorilla/public" resource-path "*.js")
    (when (fs/exists? ".gorilla/public/cljs-runtime")
      (copy-pattern ".gorilla/public/cljs-runtime" (str resource-path "cljs-runtime/") "*.js")
      (copy-pattern ".gorilla/public/cljs-runtime" (str resource-path "cljs-runtime/") "*.js.map"))))

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
  (ensure-directory ".gorilla")
  (fs/delete-tree ".gorilla/static")
  (ensure-directory ".gorilla/static")
  (save-resources)
  (write-static-config frontend-config)
  (generate-static-html frontend-config)
 ; (ensure-directory resource-path)
  (ensure-directory (str resource-path "cljs-runtime"))
  (copy-js)
;  
  )
