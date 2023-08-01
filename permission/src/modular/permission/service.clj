(ns modular.permission.service
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [modular.permission.user :refer [get-user]]
   [modular.permission.role :as role] ))


(def permissioned-services (atom {}))

(defn add-permissioned-services [perm-table]
  ; {:time/now-date #{} 
  ;  :time/local nil}
  (debug "add permissioned services: " (keys perm-table))
  (swap! permissioned-services merge perm-table)
  (debug "permissioned table: " @permissioned-services)
  )

(defn has-permission-for-service [service-kw-or-symbol]
  (contains? @permissioned-services service-kw-or-symbol))

(defn required-permission-for-service [service-kw-or-symbol]
  (get @permissioned-services service-kw-or-symbol))

(defn service-authorized? [service-kw user-id]
  (info "service-authorized? service: " service-kw " user-id: " user-id)
  (let [user (get-user user-id)
        has-permission? (has-permission-for-service service-kw)
        required-roles (required-permission-for-service service-kw)
        a? (if (and user has-permission?)
             (role/authorized? required-roles user-id)
             false)]
    (cond
      (not has-permission?)
      (do (error "service-authorized? " service-kw " - no permission entry! - please define permissions for this service!")
          false)
      
      (nil? required-roles)
      true  ; if the service does not require anything, then authorized.
      
      (not user)
      (do (warn "service-authorized? " service-kw " - user unknown: " user-id)
          false)

      a?
      true

      (not a?)
      (do (warn "service-authorized? " service-kw " - not permissioned for user: " user-id)
          false)

      :else ; should not happen, just to be safe.
      false)))
      
      
