(ns deps
  (:require
   [clojure.edn :as edn]
   ;[fipp.clojure]
   ))

;(defn pr-str-fipp [config]
;  (with-out-str
;    (fipp.clojure/pprint config {:width 40})))

(defn load-deps-config-path [edn-path key-path]
  (-> (slurp (str edn-path "/deps.edn"))
      (edn/read-string)
      (get-in key-path)))

(defn merge-deps [main others to]
  (let [main-edn (load-deps-config-path main [])
        main-deps (:deps main-edn)
        others-deps-list (map #(load-deps-config-path % [:deps]) others)
        other-deps (apply concat others-deps-list)
        all-deps (into [] (concat main-deps other-deps))
        merged-edn (assoc main-edn :deps all-deps)]
    (println "merged deps: " all-deps)
    (->> merged-edn
         (pr-str)
         (spit to))))


(comment

  (merge-deps "./profiles/webly"
              ["./profiles/frontend"
               "./profiles/webserver"
               "./profiles/websocket"
               "./profiles/oauth2"]
              "./deps.edn")


  ;
  )
