(ns webly.build.bundle-size
  (:require
   [clojure.string]
   [clojure.java.io :as io]
   [shadow.cljs.build-report]))

(defn generate-bundlesize-report [config]
  (with-redefs [shadow.cljs.devtools.api/get-build-config
                (fn [_ #_build-id]
                  (get-in config [:builds :webly]))]
    (shadow.cljs.build-report/generate
     :webly
     {:print-table true
      :report-file "target/bundlesizereport.html"})))




