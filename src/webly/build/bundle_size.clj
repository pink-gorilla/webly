(ns webly.build.bundle-size
  (:require
   [clojure.string]
   [clojure.java.io :as io]
   [shadow.cljs.build-report]))

(defn generate-bundlesize-report []
  (shadow.cljs.build-report/generate
   :webly
   {:print-table true
    :report-file ".webly/bundlesizereport.html"}))







