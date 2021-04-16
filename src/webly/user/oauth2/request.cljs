(ns webly.user.oauth2.request
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [clojure.string :as str]
   [ajax.core :as ajax]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [webly.prefs :refer [pref]]
   [webly.log :refer [timbre-config!]]
   [webly.user.notifications.core :refer [add-notification]]))

(defn req [provider token]
  (case provider
    :google
    {:uri (str "https://www.googleapis.com/oauth2/v2/userinfo")
     :headers {"Authorization" (str "Bearer " token)}}
    :github
    {:uri "https://api.github.com/user"
     :headers {"Authorization" (str "token " token)}}))

;'authorization: Bearer YOUR_ACCESS_TOKEN' 
;$ curl -H "Authorization: token OAUTH-TOKEN" https://api.github.com


(reg-event-fx
 :request
 (fn [{:keys [db]} [_ provider]]

   (let [token (get-in db [:token provider])
         at (:access-token token)
         r (merge (req provider at)
                  {:method          :get
                   :timeout         5000                     ;; optional see API docs
                   :response-format  (ajax/json-response-format {:keywords? true}) ;; IMPORTANT!: You must provide this.
                   :on-success      [:request/success provider]
                   :on-failure      [:request/error provider]})]
     (info "making web request:" provider " token: " at " r: " r)
     {;:db       (assoc-in db [:pref] (pref))
      :http-xhrio r})))

  ;:format (ajax/json-request-format) ;  {:keywords? false})
  ;:response-format; {:keywords? true})

(reg-event-fx
 :request/success
 (fn [{:keys [db]} [_ provider data]]
   (info "request success:" provider data)
   {}))

(defn err-msg [res]
  (or (get-in res [:response :error :message])
      (get-in res [:response :message])))

(reg-event-fx
 :request/error
 (fn [{:keys [db]} [_ provider res]]
   (info "request error: " provider res)
   (add-notification :danger (str "request error " provider ": " (err-msg res)))
   {}))

