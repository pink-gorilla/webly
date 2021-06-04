(ns webly.writer
  (:require
   [clojure.java.io :as io]
   [fipp.clojure]
   [webly.date :refer [now-str]]))

(defn write [filename data]
  (let [comment (str "; generated by webly on " (now-str) "\r\n")
        s (with-out-str
            (fipp.clojure/pprint data {:width 60}))
        s (str comment s)]
    (spit filename s)))

(defn ensure-directory-webly []
  (when-not (.exists (io/file ".webly"))
    (.mkdir (java.io.File. ".webly"))))

(defn write-status [name data]
  (ensure-directory-webly)
  (let [filename (str ".webly/" name ".edn")]
    (write filename data)))



; fast, but no pretty-print (makes it difficult to detect bugs)


#_(defn write-shadow-config [config]
    (spit "shadow-cljs.edn" (pr-str config)))

; pretty, but slower
(defn write-shadow-config [config]
  (let [filename "shadow-cljs.edn"]
    (write filename config)))