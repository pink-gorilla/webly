(ns modular.oauth2.authorize.token-events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx reg-event-db dispatch]]
   [ajax.core :as ajax]
   [modular.oauth2.provider :refer [get-provider-config url-redirect]]))

(defn  current-url []
  (-> js/window .-location .-href))

(reg-event-fx
 :oauth2/code->token
 (fn [{:keys [db]} [_ p data]]
   (info "getting token for provider " p " with data: " data)
   (let [provider (get-provider-config p)
         code (:code data)
         {:keys [client-id client-secret]} (get-in db [:config :oauth2 p])]
     (info "github code -> token .. code: " code " clientId " client-id)
     {:db       db
      :http-xhrio {:method          :get
                   :uri            "/api/oauth2/token" ;  "https://github.com/login/oauth/access_token"
                   :params {:provider p
                            :code code
                            :url-redirect (url-redirect p (current-url))}
                   ;:format (ajax/json-request-format {:keywords? true})
                   :timeout         5000                     ;; optional see API docs
                   :response-format (ajax/json-response-format {:keywords? true});; IMPORTANT!: You must provide this.
                   :on-success      [:oauth2/save-token p]
                   :on-failure      [:oauth2/save-error p]}})))

