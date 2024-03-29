(ns demo.test
  (:require
   [extension :refer [discover write-service]]
   [webly.module.build :refer [create-modules shadow-module-config]]))

(defn test [& _]
  (println "creating modules..")
  (let [exts (discover)
        modules (create-modules exts)
        shadow-modules (shadow-module-config modules)
        ]
    (write-service exts :shadow-modules shadow-modules)
    (println "creating modules..done!")))

