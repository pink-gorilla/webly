(ns webly.build.static
  (:require
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

(defn generate-static-html []
  (create-html app-page-static "/r/webly.js" "target/webly/public/index.html")
  (create-html app-page-dynamic "/r/webly.js" "target/webly/public/index_dynamic.html"))

(defn save-resources []
  (write-resources-to "target/webly/public/r" "public"))

(defn write-static-config []
  (let [filename "target/webly/public/config.edn"
        config (-> @config-atom
                   (dissoc :oauth2) ; oauth2 settings are private
                   )]
    (write filename config)))

(defn prepare-static-page []
  (generate-static-html)
  (save-resources)
  (write-static-config))