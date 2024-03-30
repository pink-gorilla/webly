(ns demo.test
  (:require
   [extension :refer [discover write-service]]
   [webly.module.build :refer [create-modules shadow-module-config
                               create-modules get-loadables
                               get-lazy-ns
                               ]]))

(defn test [& _]
  (println "creating modules..")
  (let [exts (discover)
        modules (create-modules exts)
        shadow-modules (shadow-module-config modules)]
    (write-service exts :shadow-modules shadow-modules)
    (println "creating modules..done!")))


(comment 
  
  (def exts (discover))

  (def modules (create-modules exts))
  
  (macroexpand-1
   (get-loadables)
   )
  
  (get-lazy-ns)
  (macroexpand (get-lazy-ns))
  
 ; 
  )
