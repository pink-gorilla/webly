(ns webly.web.files
  (:require
   [taoensso.timbre  :refer [debug info warn error]]
   [bidi.bidi :as bidi :refer [url-decode]]
   [clojure.java.io :as io]
   [ring.util.response :refer [file-response resource-response]]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.not-modified :refer [wrap-not-modified]]))

(defrecord FilesMaybe [options]
  bidi/Matched
  (resolve-handler [this m]
    (let [reminder (url-decode (:remainder m))
          filename (str (:dir options) reminder)]
      ;(warn "file-maybe: " filename)
      (when (.exists (io/file filename))
        ;(warn "file found: " filename)
        (assoc (dissoc m :remainder)
               :handler (->
                         (fn [req] (file-response reminder
                                                  {:root (:dir options)}))
                         (wrap-content-type options)
                         (wrap-not-modified))))))
  (unresolve-handler [this m]
    (when (= this (:handler m)) "")))


; copied from bidi.
; but bidi forgot to wrap not modified
; https://github.com/juxt/bidi/issues/208


(defrecord ResourcesMaybe [options]
  bidi/Matched
  (resolve-handler [this m]
    (let [path (url-decode (:remainder m))]
      (when (not-empty path)
        (when-let [res (io/resource (str (:prefix options) path))]
          ;(warn "res: " path)
          (assoc (dissoc m :remainder)
                 :handler (->
                           (fn [req] (resource-response (str (:prefix options) path)))
                           (wrap-content-type options)
                           (wrap-not-modified) ; awb99 hack
                           ))))))
  (unresolve-handler [this m]
    (when (= this (:handler m)) "")))