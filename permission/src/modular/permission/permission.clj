(ns modular.permission.role
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.set :refer [superset?]]
   [modular.permission.user :refer [get-user-roles]]
   ))


(defn get-user-roles [user-id]
  (if-let [user (get-user user-id)]
    (if-let [roles (:roles user)]
      roles
      #{})
    #{}))

(defn authorized? [required-roles user-roles]
    (superset? (get-user-roles user-id) required-roles)
      false)
    true ; no required-roles means authorized
    ))


