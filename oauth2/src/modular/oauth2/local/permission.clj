(ns modular.oauth2.local.permission
 (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.set :refer [superset?]]
   [modular.config :refer [get-in-config]]
  ))

(defn get-user-roles [user]
 (if-let [users (get-in-config [:users])]
   (if user
     (let [user-kw (keyword user)
           user-data (user-kw users)]
      (if user-data
        (:roles user-data)
        #{}))
      (do (info "get-urser-roles for nil user")
          nil)) 
  (do (error "user roles missing: please add :users to config!")
       #{})))

(defn authorized? [required-roles user]
  (if required-roles
    (if user 
      (superset? (get-user-roles user) required-roles)
      false)
    true ; no required-roles means authorized
    ))


