(ns modular.rest.helper
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [clojure.data.json :as json]
   [cheshire.core]))

(defn wrap-exception []
  (try

    (+ 4 5)

    (catch clojure.lang.ExceptionInfo e
      (error "get-request " provider_endpoint " error: " (.getMessage e))
      ;(warn (.getData e))
      ; (info (prn e))
      (when-let [body (-> e .getData :body)]
        (let [b (cheshire.core/parse-string body true)]
          (warn "body: " b)
          b)))
    (catch Exception e
      (error "get-request " provider_endpoint " exception")
      ;(error e)
      (error "keys of error: " (keys e)))
;
    ))