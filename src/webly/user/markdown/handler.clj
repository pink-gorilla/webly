(ns webly.user.markdown.handler
  (:require
   [resauce.core]
   [clojure.string :refer [split]]
   [taoensso.timbre :as timbre :refer [tracef debugf info infof warnf errorf info]]
   [ring.util.response :as res])
  (:import
   (java.net URL)))

(defn file [^URL input]
  (-> input
      (.getPath)
      (split #"/")
      (last)))

(defn get-md-files []
  (map file
       (resauce.core/resource-dir "public/gorillamd")))

(defn handler-md-files
  [req]
  (info "handler-md-files")
  (let [n (get-md-files)]
    (info "md files count: " (count n))
    (res/response {:data n})))

(comment

  (get-md-files)

  ;
  )




