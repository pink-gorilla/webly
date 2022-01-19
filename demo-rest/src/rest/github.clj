(ns rest.github
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [martian.core :as martian]
   [modular.rest.martian.github :refer [martian-github]]))

(defn github []
  ; github token does not expire.
  (let [m (martian-github)]
    (->> (martian/response-for m :userinfo {})
         :body
         (info "github userinfo: "))
  
  (->> (martian/response-for m :search-repo {:q "user:pink-gorilla"})
       :body
       :items
       ;first
       (map #(select-keys % [:name :forks_count :watchers_count :open_issues_count]))
       (print-table))
       
  ;     
))