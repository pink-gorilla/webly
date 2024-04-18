(ns webly.spa.service
  (:require
   [extension :refer [write-service get-extensions]]))

;; Extension config

(defn cljs-service? [{:keys [cljs-service]}]
  cljs-service)

(defn select-service [{:keys [cljs-service name]}]
  {:name name
   :cljs-service cljs-service})

(defn- get-cljs-services [exts]
  (->> (get-extensions exts {:name "unknown"
                             :cljs-service nil})
       (filter cljs-service?)
       (map select-service)
       (into [])))

(defn cljs-services [exts]
  (let [services (get-cljs-services exts)]
    (write-service exts :cljs-services services)
    services))
