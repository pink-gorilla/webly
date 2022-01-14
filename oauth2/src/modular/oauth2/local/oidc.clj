(ns modular.oauth2.local.oidc
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [modular.config :refer [get-in-config]]))

; :florian {:roles #{:logistic}
;           :password "xxxxxxxx" 
;           :email ["hoertlehner@gmail.com"]}

(defn user-email [[user {:keys [email]}]]
  {:user user
   :email email})

(defn find-user [email]
  (if-let [users (get-in-config [:users])]
    (let [user-email-list (map user-email users)
          contains-email (fn [user]
                           (some #(= email %) (:email user)))
          _ (debug "user-email-list: " (pr-str user-email-list))
          m (-> (filter contains-email user-email-list)
                first)]
      (:user m))
    (do (error "no :users defined")
        nil)))

