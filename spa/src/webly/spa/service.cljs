(ns webly.spa.service
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [promesa.core :as p]
   [webly.spa.mode :refer [get-mode]]
   [webly.spa.resolve :refer [get-resolver]]))

(defn start-cljs-service [resolver mode {:keys [name cljs-service]}]
  (let [{:keys [init-fn]} cljs-service
        init-p (resolver init-fn)]
    (warn "starting cljs-service name: " name " init-fn: " init-fn)
    (-> init-p
        (p/then (fn [init]
                  (init mode))))))

(defn start-cljs-services [services]
  ;[{:name "demo-webly", :cljs-service {:init-fn demo.service/start}} 
  ; {:name "hello", :cljs-service {:init-fn snippets.hello/start}}]
  (let [resolver (get-resolver)
        mode (get-mode)]
    (warn "starting cljs services: " services " mode: " mode)
    (doall (map #(start-cljs-service resolver mode %) services))))

