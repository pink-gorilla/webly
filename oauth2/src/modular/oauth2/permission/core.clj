(ns modular.oauth2.permission.core
 (:require
   [clojure.set :refer [superset?]]
))

(def users
  (atom 
    {:demo {:roles #{:admin :logistic}]
            :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
            :email ["hoertlehner@gmail.com"]}}))

(defn get-user-roles [user]
 (let [user-kw (keyword user)
       user-data (user-kw users)]
  (if user-data
    (:roles user-data)
    #{})))

(defn authorized? [user required-roles]
  (superset? (get-user-roles user) required-roles))
