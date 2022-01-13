(ns modular.oauth2.user.subscriptions
  (:require
   [re-frame.core :refer [reg-sub]]))

(reg-sub
 :oauth2/tokens
 (fn [db [_]]
   (get-in db [:token])))

(defn token-expired? [token]
  (if-let [exp (:exp token)]
    (let [time-current (.getTime js/Date.)   ; if (new Date () .getTime () <= exp * 1000) return false;
          time-exp (* exp 1000)]
      (println "exp: " time-exp " cur:" time-current)
      (<= time-current time-exp))
    false))

(defn token? [token]
  (let [{:keys [access-token]} token]
    (if access-token true false)))   ;      (some? token)

(reg-sub
 :oauth2/logged-in?
 (fn [db [_ service]]
   (if-let [token (get-in db [:token service])]
     ;(token-expired? token)
     (token? token)
     false)))

(reg-sub
 :oauth2/logged-in-email-or-user
 (fn [db [_ service]]
   (let [email (get-in db [:token service :email])
         user (get-in db [:token service :user])
         eu (or email user)]
     (if eu
       eu
       "unknown email"))))