(ns webly.user.oauth2.request
  (:require
   [webly.user.notifications.core :refer [add-notification]]
   [modular.oauth2.request] ; side-effects
   ))
(defn err-msg [res]
  (or (get-in res [:response :error :message])
      (get-in res [:response :message])))

(rf/reg-event-fx
 ::oauth2/request-error
 (fn [{:keys [db]} [_ provider res]]
   (errorf "oauth2 provider: %s error: %s" provider res)
   (add-notification :error (str "request error " provider ": " (err-msg res)))
   {}))

(rf/reg-event-fx
 ::oauth2/login-error
 (fn [{:keys [db]} [_ provider res]]
   (errorf "oauth2 provider: %s error: %s" provider res)
   (add-notification :error (str "request error " provider ": " (err-msg res)))
   {}))

