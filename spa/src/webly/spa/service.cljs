(ns webly.spa.service
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [promesa.core :as p]
   [webly.spa.resolve :refer [get-resolver]]))

(defn start-cljs-service [resolver {:keys [name config start-fn]}]
  (let [r-p (p/deferred)
        start-p (resolver start-fn)]
    (warn "starting cljs-service name: " name "start-fn: " start-fn)
    (-> start-p
        (p/then (fn [start]
                  (let [r (start config)]
                    (if (p/promise? r)
                      (p/then r (fn [_] (p/resolve! r-p nil)))
                      (p/resolve! r-p nil))))))
    r-p))

(defn start-cljs-services [services]
  ;[{:name "demo-webly", :cljs-service {:init-fn demo.service/start}} 
  ; {:name "hello", :cljs-service {:init-fn snippets.hello/start}}]
  (let [resolver (get-resolver)
        _ (warn "starting cljs services: " (map :name services))
        services-all (doall (map #(start-cljs-service resolver %) services))
        r-p (p/all services-all)]
    r-p))

