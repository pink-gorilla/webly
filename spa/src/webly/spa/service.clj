(ns webly.spa.service
  (:require
   [extension :refer [get-extensions]]
   [webly.helper :refer [write-target2]]))

;; discovery

(defn cljs-service? [{:keys [cljs-service]}]
  cljs-service)

(defn select-service [{:keys [cljs-service name]}]
  (assoc cljs-service :name name))

(defn- get-cljs-services [exts]
  (->> (get-extensions exts {:name "unknown"
                             :cljs-service nil})
       (filter cljs-service?)
       (map select-service)
       (into [])))

;; config

(defn service-config [config exts {:keys [name config-fn default-config] :as service}]
  (if config-fn
    (let [configure (requiring-resolve config-fn)]
      (assoc service :config (configure name config exts default-config)))
    (assoc service :config nil)))

(defn cljs-services [config exts]
  (let [services (->> (get-cljs-services exts)
                      (map #(service-config config exts %)))]
    (write-target2 :cljs-services services)
    services))

;; simple-config

(defn cljs-config-simple [module-name config _exts default-config]
  (let [module-name (if (string? module-name)
                      (keyword module-name)
                      module-name)]
    (or (get config module-name) default-config)))