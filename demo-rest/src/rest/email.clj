(ns rest.email
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [clojure.java.io :as io]
   [modular.rest.lib.email :refer [send-email]]
))

(defn email []
 (let [r1 (send-email 
              {:to "hoertlehner@gmail.com"
               :cc ["crb.clean@gmail.com"]
               :subject "Hi!"
               :body "Test."
               :type "text/html; charset=utf-8"
               })
       _ (info "result simple: " r1)
       _ (spit "/tmp/attachment.txt" "123456")
       r2 (send-email 
              {:to "hoertlehner@gmail.com"
               :cc ["crb.clean@gmail.com"]
               :subject "Hi!"
               :body [{:type "text/html"
                       :content "<b>Test!</b>"}
                      {:type :attachment
                        :content (java.io.File. "/tmp/attachment.txt")}
                      #_{:type :inline
                       :content (java.io.File. "/tmp/a.pdf")
                       :content-type "application/pdf"}]})
       _ (info "attachment result: " r2)
 ]  
  
))