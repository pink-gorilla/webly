(ns modular.permission.user
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.pprint :refer [print-table]]
   [modular.config :refer [get-in-config]]))

; :florian {:roles #{:logistic}
;           :password "xxxxxxxx" 
;           :email ["hoertlehner@gmail.com"]}


(defn user-db []
  (if-let [users (get-in-config [:users])]
    users
    (do (error "cannot get user-db, please add [:users] in config!")
        nil)))

(defn users-count []
  (let [users (user-db)]
    (if users
      (-> users keys count)
      0)))

(defn get-user [user-id]
  (when-let [users (user-db)]
    (when-let [user (get users user-id)]
      (assoc user :id user-id))))

(defn get-user-list []
  (map get-user (keys (user-db))))

; find user by email

(defn- user-email [[user-id {:keys [email]}]]
  {:user-id user-id
   :email email})

(defn find-user-id-via-email [email]
  (if-let [users (user-db)]
    (let [user-email-list (map user-email users)
          contains-email (fn [user]
                           (some #(= email %) (:email user)))
          _ (debug "user-email-list: " (pr-str user-email-list))
          m (-> (filter contains-email user-email-list)
                first)]
      (:user-id m))
    nil))


(defn print-users []
  (->> (get-user-list)
       (print-table [:id :roles :email])))

