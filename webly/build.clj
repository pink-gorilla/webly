(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [deps-deploy.deps-deploy :as dd]))


(def lib 'org.pinkgorilla/webly)
(def version (format "0.5.%s" (b/git-count-revs nil)))

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

