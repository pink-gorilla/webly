(ns webly.helper
  (:require
   [modular.writer :refer [write write-status write-target ensure-directory]]))

(defn write-target2 [name data]
  (ensure-directory "./target")
  (ensure-directory "./target/webly")
  (let [filename (str "./target/webly/" name ".edn")]
    (write filename data)))