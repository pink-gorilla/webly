(ns modular.permission.role
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.set :refer [superset?]]
   [modular.permission.user :refer [get-user]]
   ))

(defn get-user-roles [user-id]
  (if-let [user (get-user user-id)]
    (if-let [roles (:roles user)]
      roles
      #{})
    nil)) ; no user, no roles.


(defn- user-has-role [user-roles role]
  (contains? user-roles role))

(defn- user-has-one-role-that-service-requires [user-roles required-roles]
  (let [r (some (partial user-has-role user-roles) required-roles)]
    (if r true false)))


(defn authorized-roles? [required-roles user-roles]
  (cond
    (nil? required-roles)
    true

    (empty? required-roles)
    (if user-roles true false)
    
    :else
    (user-has-one-role-that-service-requires user-roles required-roles)
    ))

(defn authorized? [required-roles user-id]
  (let [user-roles (get-user-roles user-id)]
     (authorized-roles? required-roles user-roles)))

(comment

  (user-has-role #{:logistic :admin} :admin)
  (user-has-role #{:logistic :admin} :management)

  (authorized-roles? nil #{}) ; true = route does not need roles
  (authorized-roles? nil #{:admin}) ; true = route does not need roles
  (authorized-roles? nil nil) ; true = route does not need roles.

  (authorized-roles? #{} #{}) ; route needs login, and user is logged in
  (authorized-roles? #{} nil) ; route needs login, but user not logged in
  
  (user-has-one-role-that-service-requires #{} #{})

  (require '[modular.config :as config])
  (config/set! :users
    {:demo {:roles #{:admin :logistic}
            :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
            :email ["john@doe.com"]}})

  (authorized? #{:admin} :demo) ; demo has admin role => true
  (authorized? #{:accounting} :demo) ; demo does not have accouting role => false
  (authorized? #{:accounting :management} :demo) ; demo does not have accouting role => false
  (authorized? #{:accounting :logistic} :demo) ; demo does not logistic role => true
  (authorized? #{:admin} :hacker)  ; hacker has no roles
  (authorized? #{:admin} nil)  ; not logged in user has no roles

;
  )

