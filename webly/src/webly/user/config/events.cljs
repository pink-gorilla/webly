(ns webly.user.config.events
  "Events related configuration loading"
  (:require
   [taoensso.timbre :refer-macros [debug info infof error]]
   ;[clojure.string :as str]
   [ajax.core :as ajax]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [modular.log :refer [timbre-config!]]
   [modular.encoding.transit :refer [decode]]
   [webly.prefs :refer [pref]]
   [webly.user.notifications.core :refer [add-notification]]))

; load configuration

(reg-event-fx
 :config/load
 (fn [{:keys [db]} [_ after-config-load]]
   (infof "loading config from server (dispatch after-load: %s)" after-config-load)
   {:db   (assoc-in db [:pref] (pref))
    :http-xhrio {:method          :get
                 :uri             "/api/config"
                 :timeout         10000                     ;; optional see API docs
                 :response-format (ajax/transit-response-format :json decode) ;; IMPORTANT!: You must provide this.
                 :on-success      [:config/load-success after-config-load]
                 :on-failure      [:config/load-error]}}))

(reg-event-fx
 :config/load-success
 (fn [cofx [_ after-config-load config]]
   (let [fx {:db          (assoc-in (:db cofx) [:config] config)
             :dispatch [after-config-load]}]
     (info "config load-success!")
     (timbre-config! config)
     (debug "config: " config)
     (if after-config-load
       fx
       (dissoc fx :dispatch)))))

(reg-event-db
 :config/load-error
 (fn [db [_ response]]
   (error "config-load-error: " response)
   (let [details (str (:status-text response) " (" (:status response) ")")]
     (add-notification :danger "Error loading config")
     db)))
