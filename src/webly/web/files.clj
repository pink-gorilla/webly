(ns webly.web.files
  (:require
   [taoensso.timbre  :refer [debug info error]]
   [bidi.bidi :as bidi :refer [url-decode]]
   [clojure.java.io :as io]
   [ring.util.response :refer [file-response]]
   [ring.middleware.content-type :refer (wrap-content-type)]
   [ring.middleware.not-modified :refer (wrap-not-modified)]))

(defrecord FilesMaybe [options]
  bidi/Matched
  (resolve-handler [this m]
    (assoc (dissoc m :remainder)
           :handler (let [reminder (url-decode (:remainder m))
                          filename (str (:dir options) reminder)]
                      ;(info "file-maybe: " filename)
                      (when (.exists (io/file filename))
                        ;(info "file found: " filename)
                        (->
                         (fn [req] (file-response reminder
                                                  {:root (:dir options)}))
                         (wrap-content-type options)
                         (wrap-not-modified))))))
  (unresolve-handler [this m]
    (when (= this (:handler m)) "")))