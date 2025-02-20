(ns build
  (:require
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb])) ; https://github.com/seancorfield/build-clj
   

(def lib 'org.pinkgorilla/webly)
(def version (format "0.9.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building hte JAR")
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "pom-template.xml"
             :transitive true)
      (bb/jar)))


(defn deploy "Deploy the JAR to Clojars." [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib 
             :version version)
      (bb/deploy)))

