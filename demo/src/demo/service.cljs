(ns demo.service
  (:require
   [taoensso.timbre :as timbre :refer [info warn]]))

(defn start [mode]
  (info "demo.service/start mode: " mode)
  nil)