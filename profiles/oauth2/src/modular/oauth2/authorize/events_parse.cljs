(ns modular.oauth2.authorize.events-parse
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [ajax.core :as ajax]))

(reg-event-fx
 :github/code->token
 (fn [{:keys [db]} _]
   (let [code (get-in db [:token :github :code])
         {:keys [client-id client-secret]} (get-in db [:config :oauth2 :github])]
     (info "github code -> token .. code: " code " clientId " client-id)
     {:db       db
      :http-xhrio {:method          :get
                   :uri            "/api/oauth2/github/token" ;  "https://github.com/login/oauth/access_token"
                   :params {;:client_id	 clientId
                            ;:client_secret clientSecret
                            :code code}
                   ;:format (ajax/json-request-format {:keywords? true})
                   :timeout         5000                     ;; optional see API docs
                   :response-format (ajax/json-response-format {:keywords? true});; IMPORTANT!: You must provide this.
                   :on-success      [:oauth2/save-token :github]
                   :on-failure      [:oauth2/save-error]}})))

