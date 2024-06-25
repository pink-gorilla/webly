(ns demo.handler.time
  (:require
   [ring.util.response :as res]
   [modular.date :refer [now now-local now-unix]]))

(defn time-handler [_req]
  (res/response {:unix
                 (now-unix)}))

(defn time-java-handler [_req]
  (res/response {:unix (now-unix)
                 :java (now)
                 :local (now-local)}))