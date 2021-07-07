(ns webly.build.build-redef
  (:require
   [shadow.cljs.devtools.config]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]
   [shadow.cljs.devtools.server :as shadow-server]
   [webly.build.shadow :refer [generate-bundlesize-report]]))

(defn generate-bundlesize-report [config]
  (with-redefs [shadow.cljs.devtools.api/get-build-config
                (fn [_ #_build-id]
                  (get-in config [:builds :webly]))]
    (generate-bundlesize-report)))

(defn load-cljs-edn [config]
  (-> config
      (shadow.cljs.devtools.config/normalize)
      (->> (merge shadow.cljs.devtools.config/default-config))
      (update :builds #(merge shadow.cljs.devtools.config/default-builds %))
      (assoc :user-config (shadow.cljs.devtools.config/load-user-config))))

(defn build [mode config]
  (let [opts {:verbose true}]
    (with-redefs [shadow.cljs.devtools.config/load-cljs-edn #(load-cljs-edn config)]
      (shadow-server/start! config)
      (case mode
        :compile (do (shadow/compile* (get-in config [:builds :webly]) opts)
                     (generate-bundlesize-report config)
                     (shadow-server/stop!))
        :watch (shadow/watch (get-in config [:builds :webly]) opts)))))