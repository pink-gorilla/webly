(ns demo.test
  (:require
   [extension :refer [discover]]
   [webly.module.build :refer [create-modules]]))

(defn test [& _]
  (let [exts (discover)]
    (println "creating modules..")
    (create-modules exts)
    (println "creating modules..done!")))

