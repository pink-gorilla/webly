(ns webly.user.markdown.handler
  (:require
   [resauce.core]
   [clojure.string :refer [split]]
   [taoensso.timbre :as timbre :refer [tracef debug debugf info infof warnf errorf info]]
   [ring.util.response :as res]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.web.handler :refer [add-ring-handler]])
   ;(:import (java.net URL))
  )

(defn file [;^URL 
            input]
  (debug "processing md resource: " input)
  (let [str (if (string? input)
              input
              (.getPath input))] ; resources of current project are passed as urls, of jars as strings 
    (-> str
        (split #"/")
        (last))))

(defn get-md-files []
  (map file
       (resauce.core/resource-dir "public/gorillamd")))

(defn handler-md-files
  [req]
  (debug "handler-md-files")
  (let [n (get-md-files)]
    (info "handler-md-files - count: " (count n))
    (res/response {:data n})))

(add-ring-handler :api/md (wrap-api-handler handler-md-files))

(comment

  (get-md-files)

  ;
  )




