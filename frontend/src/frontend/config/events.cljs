(ns frontend.config.events
  "Events related configuration loading"
  (:require
   [taoensso.timbre :refer-macros [debug info infof error]]
   [ajax.core :as ajax]
   [cljs.reader :refer [read-string]]
   [bidi.bidi]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [modular.log :refer [timbre-config!]]
   [modular.encoding.transit :refer [decode]]
   [modular.encoding.edn :refer [read-edn]]
   [frontend.notifications.core :refer [add-notification]]
   [frontend.routes :refer [static-main-path-atom]]
  ; [webly.build.prefs :refer [pref]]
   ))

; load configuration

(reg-event-fx
 :config/load
 (fn [{:keys [db]} [_ after-config-load static?]]
   (let [format (if static?
                  (ajax/text-response-format)
                  (ajax/transit-response-format :json decode))
         uri (if static?
               (str @static-main-path-atom "/r/config.edn")
               "/api/config")]
     (infof "loading config static?: %s from url: %s then dispatch: %s" static? uri after-config-load)
     {;:db   (assoc-in db [:build] (pref))
      :http-xhrio {:method          :get
                   :uri             uri
                   :timeout         10000                     ;; optional see API docs
                   :response-format format ;; IMPORTANT!: You must provide this.
                   :on-success      [:config/load-success static? after-config-load]
                   :on-failure      [:config/load-error]}})))

;(def config
;  {'bidi.bidi.TaggedMatch bidi/map->TaggedMatch})

;(defn read-str [s]
;  (read-string
;   {:readers config} s))

(reg-event-fx
 :config/load-success
 (fn [cofx [_ static? after-config-load config]]
   (let [config (if static?
                  (read-edn config) ; (cljs.reader/read-string config)
                  config)
         config (assoc config
                       :static? static?
                       ;:build (pref)
         ;              :mode @webly-mode-atom
         ;              :entry-path @entry-path-atom
                       )
         fx {:db (assoc-in (:db cofx) [:config] config)
             :dispatch [after-config-load static?]}]
     (info "config load-success!")
     (timbre-config! (:timbre/cljs config))
     (debug "config: " config)
     (if after-config-load
       fx
       (dissoc fx :dispatch)))))

(reg-event-db
 :config/load-error
 (fn [db [_ response]]
   (error "config-load-error: " response)
   (let [details (str (:status-text response) " (" (:status response) ")")]
     (add-notification :error "Error loading config")
     (error "config load error: " details)
     db)))
