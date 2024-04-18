(ns webly.spa.service
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [promesa.core :as p]
   [webly.spa.resolve :refer [get-resolver]]))

(defn start-cljs-service [resolver {:keys [name config start-fn]}]
  (let [start-p (resolver start-fn)]
    (warn "starting cljs-service name: " name "start-fn: " start-fn)
    (-> start-p
        (p/then (fn [start]
                  (start config))))))

(defn start-cljs-services [services]
  ;[{:name "demo-webly", :cljs-service {:init-fn demo.service/start}} 
  ; {:name "hello", :cljs-service {:init-fn snippets.hello/start}}]
  (let [resolver (get-resolver)]
    (warn "starting cljs services: " (map :name services))
    (doall (map #(start-cljs-service resolver %) services))))

