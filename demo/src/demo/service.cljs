(ns demo.service
  (:require
   [promesa.core :as p]
   [taoensso.timbre :as timbre :refer [info]]))

(defn start [config]
  (info "demo.service/start config: " config)
  (p/resolved nil) ; returning a promise means we wait till promise is resolved.
  )