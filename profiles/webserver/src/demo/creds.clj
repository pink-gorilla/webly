(ns demo.https
  (:require
   [clojure.java.io :as io]
   [sv.letsencrypt.pem-to-keystore]
   [sv.letsencrypt.core :refer [create-keystore letsencrypt-via-http-stack]]
))


(defn- compose
  "Same as `clojure.core/comp` but reverse the arguments / `fns`."
  [fns]
  (apply comp (reverse fns)))

(def dev-letsencrypt-via-http-stack
  (map
   (fn [f]
     (fn [context]
       (println "step:"
                (class f))
       (f context)))
   letsencrypt-via-http-stack))

(defn get-certificates [& _]
  (let [sample-context
        {:folder (io/file "example-results")
         :domains ["wien.hoertlehner.com"]
         ;;:staging true
         :fulfill-http-challenge (fn [http-challenge-data]
                                   (println "http-challenge-data:")
                                   (prn http-challenge-data)
                                   (println "press a key to continue")
                                   (read))}
        result-context ((compose dev-letsencrypt-via-http-stack)
                        sample-context)]
    (create-keystore result-context)))