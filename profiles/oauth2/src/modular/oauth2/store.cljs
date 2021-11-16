(ns modular.oauth2.store
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]))

(defn safe [token]
  (if-let [access-token (:access_token token)]
    (-> token
        (rename-keys {:access_token :access-token})
        ;(assoc :access-token access-token)
        ;(dissoc :acess_token)
        )
    token))

(rf/reg-event-db
 :oauth2/save-token
 (fn [db [_ provider token]]
   (let [token (safe token)
         access-token (:access-token token)
         oauth-window (:oauth-window db)]
     (info "saving token " provider token)
     (if access-token
       (do
         (rf/dispatch [:oauth2/get-user provider])
         (when oauth-window
           (.close oauth-window))
         (-> db
             (assoc-in [:token provider] token)
             (dissoc :oauth-window)))
       (assoc-in db [:token provider] token)))))