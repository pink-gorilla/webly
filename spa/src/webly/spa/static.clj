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



(defn copy-pattern [from dest p]
  (let [files (fs/glob from p)]
    (doall (map #(fs/copy % dest {:replace-existing true}) files))))

(defn copy-js [version]
  (let [js-dir (str ".gorilla/public/" version)
        dest-dir (str ".gorilla/site/" version "/r/" version)]
    (when (fs/exists? js-dir)
      (info "copying .js files..")
      (ensure-directory dest-dir)
      (copy-pattern js-dir dest-dir "*.js")
      (let [runtime-dir (str js-dir "/cljs-runtime")
            dest-runtime-dir (str dest-dir "/cljs-runtime/")]
        (when (fs/exists? runtime-dir)
          (ensure-directory dest-runtime-dir)
          (copy-pattern runtime-dir dest-runtime-dir "*.js")
          (copy-pattern runtime-dir dest-runtime-dir "*.js.map"))))))

(defn generate-static-html [frontend-config filename]
  (info "generating static html page ..")
  (let [csrf-token "llXxTmFvjm6KXhKBjY7nemz4GNRwF/ZgZbycGDgw8cdF1B/cbmX5JZElY3MCnyEYUUGCi7Cw3k3mUpMI"
        ;filename (str root page-name ".html")
        ]
    (info "writing static page: " filename)
    (->> (app-page-static frontend-config csrf-token)
         (spit filename))))

(defn save-resources [dir]
  (info "exporting resources..")
  (write-resources-to dir "public")
  (fs/move (str dir "/public") (str dir "/r/"))
  )

(defn build-static [frontend-config version]
  (let [dir (str ".gorilla/site/" version)]
    (fs/delete-tree dir)
    (ensure-directory dir)
    ; 1. index.html
    (generate-static-html frontend-config (str dir "/index.html"))
    ; 2. resources from classpath
    (save-resources dir)  
    ; 3. compiled cljs
    (copy-js version)
    ; 4. config.edn
    (write (str dir "/r/config.edn") frontend-config)
    
    
    )
   
  )
