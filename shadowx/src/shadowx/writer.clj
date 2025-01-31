(ns shadowx.writer
  (:require
   [modular.writer :refer [ensure-directory]]))

(defn write-target2 [name data]
  (ensure-directory "./target")
  (ensure-directory "./target/webly")
  (let [filename (str "./target/webly/" name ".edn")]
    (modular.writer/write filename data)))

(def write 
  modular.writer/write)