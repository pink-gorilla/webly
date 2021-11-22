(ns webly.user.oauth2.request
  (:require
   [taoensso.timbre :refer-macros [info errorf]]
   [re-frame.core :as rf]
   [frontend.notifications.core :refer [add-notification]]
   [modular.oauth2.request] ; side-effects
   ))

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

