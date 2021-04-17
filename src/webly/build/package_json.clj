(ns webly.build.package-json
  (:require
   [clojure.java.io :as io]
   [taoensso.timbre :as timbre :refer [info]]
   [cheshire.core :as cheshire]))

(def default-config
  {:private true
   :scripts {:test "karma start --single-run"}
   :dependencies {}
   :devDependencies {}})

(defn generate-config [config]
  (let [filename "package.json"
        my-pretty-printer (cheshire/create-pretty-printer
                           (assoc cheshire/default-pretty-print-options
                                  :indent-arrays? true))]
    (spit filename (cheshire/generate-string config {:pretty my-pretty-printer}))))

(defn ensure-package-json []
  (when (not (.exists (io/file "package.json")))
    (info "auto creating package.json")
    (generate-config default-config)))

(defn load-res [res-name]
  (let [data-file (io/resource res-name)]
    (slurp data-file)))

(defn ensure-karma []
  (let [file-name "karma.conf.js"
        res-name (str "webly/" file-name)]
    (when (not (.exists (io/file file-name)))
      (info "copying from resources: " file-name)
      (let [content (load-res res-name)]
        (spit file-name content)))))