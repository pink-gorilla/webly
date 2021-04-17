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