(ns modular.oauth2.token.save-handler
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

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