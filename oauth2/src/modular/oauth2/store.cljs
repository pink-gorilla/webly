(ns modular.oauth2.store
  (:require
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

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
         (rf/dispatch [:oauth2/save-server provider token])
         (when oauth-window
           (.close oauth-window))
         (-> db
             (assoc-in [:token provider] token)
             (dissoc :oauth-window)))
       (assoc-in db [:token provider] token)))))

(rf/reg-event-fx
 :oauth2/save-result-success
 (fn [_ [_ provider]]
   (info "server token save success for provider" provider)
   nil))

(rf/reg-event-fx
 :oauth2/save-result-error
 (fn [_ [_ provider]]
   (error "server token save error  for provider" provider)
   nil))

(rf/reg-event-fx
 :oauth2/save-server
 (fn [{:keys [db]} [_ provider token]]
   (info "saving token for provider " provider " on server")
   {:http-xhrio {:method          :post
                 :uri            "/api/oauth2/save-token"
                 :params {:provider provider
                          :token token}
                   ;:params {;:client_id	 clientId
                   ;         ;:client_secret clientSecret
                   ;         :code code}
                 :format (ajax/transit-request-format)
                 :timeout         5000                     ;; optional see API docs
                 :response-format (ajax/transit-response-format);; IMPORTANT!: You must provide this.
                 :on-success      [:oauth2/save-result-success]
                 :on-failure      [:oauth2/save-result-error]}}))