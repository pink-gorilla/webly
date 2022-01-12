(ns modular.oauth2.local.permission
 (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.set :refer [superset?]]
   [modular.config :refer [get-in-config]]
  ))

(defn get-user-roles [user]
 (if-let [users (get-in-config [:users])]
   (let [user-kw (keyword user)
         user-data (user-kw users)]
    (if user-data
      (:roles user-data)
      #{}))
   (do (error "user roles missing: please add :users to config!")
       #{})))

(defn authorized? [required-roles user]
  (superset? (get-user-roles user) required-roles))
