(ns demo.password
  (:require
   [modular.oauth2.local.pass :refer [pwd-hash]]))

(defn password [{:keys [password]}] 
  (println "password " password " has hash: " (pwd-hash password)))
