(ns webly.ws.server
  (:require
   [clojure.string]
   [org.httpkit.server :as httpkit]
   [webly.ws :refer [start-router!]]))

(def server (atom nil))

(defn server-start!
  "starts webserver with websockets"
  [config handler]
  (let [httpkit-config (:httpkit config)
        {:keys [port]} httpkit-config]
    (println "starting web at " port)
    (httpkit/run-server handler {:port port})
    (start-router!)
    (reset! server nil)))