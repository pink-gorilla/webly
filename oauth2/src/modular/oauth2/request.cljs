(ns modular.oauth2.request
  (:require
   [taoensso.timbre :refer-macros [info errorf]]
   [clojure.string :as str]
   [ajax.core :as ajax]
   [re-frame.core :as rf]
   [modular.oauth2.provider :refer [get-provider-auth-header]]
   [frontend.notifications.core :refer [add-notification]]))

;'authorization: Bearer YOUR_ACCESS_TOKEN' 
;$ curl -H "Authorization: token OAUTH-TOKEN" https://api.github.com

(rf/reg-event-fx
 :request
 (fn [{:keys [db]} [_ provider uri success]]
   (let [token (get-in db [:token provider])
         at (:access-token token)
         r (merge {:headers (get-provider-auth-header provider at)}
                  {:uri uri
                   :method          :get
                   :timeout         5000                     ;; optional see API docs
                   :response-format  (ajax/json-response-format {:keywords? true}) ;; IMPORTANT!: You must provide this.
                   :on-success      [success provider]
                   :on-failure      [:oauth2/request-error provider]})]
     (info "making web request:" provider " token: " at " r: " r)
     {:http-xhrio r})))

  ;:format (ajax/json-request-format) ;  {:keywords? false})
  ;:response-format; {:keywords? true})

(defn err-msg [res]
  (or (get-in res [:response :error :message])
      (get-in res [:response :message])))

(rf/reg-event-fx
 :oauth2/request-error
 (fn [{:keys [db]} [_ provider res]]
   (errorf "oauth2 provider: %s error: %s" provider res)
   (add-notification :error (str "request error " provider ": " (err-msg res)))
   {}))

(rf/reg-event-fx
 :oauth2/login-error
 (fn [{:keys [db]} [_ provider]]
   (errorf "oauth2 provider: %s error" provider)
   (add-notification :danger "oauth login error (token not received)")
   {}))

