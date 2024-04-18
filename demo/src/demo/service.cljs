(ns demo.service
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]))

(defn start [config]
  (info "demo.service/start config: " config)
  nil)