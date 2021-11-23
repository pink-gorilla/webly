(ns frontend.config.events
  "Events related configuration loading"
  (:require
   [taoensso.timbre :refer-macros [debug info infof error]]
   ;[clojure.string :as str]
   [ajax.core :as ajax]
   [cljs.reader :refer [read-string]]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [modular.log :refer [timbre-config!]]
   [modular.encoding.transit :refer [decode]]
   [frontend.notifications.core :refer [add-notification]]
   [webly.prefs :refer [pref]]
   [frontend.config.core :refer [webly-mode-atom entry-path-atom]]
   [frontend.helper :refer [static-config-url]]
   [bidi.bidi]
   )
 ;  (:import [bidi.bidi TaggedMatch])
  )

; load configuration

(reg-event-fx
 :config/load
 (fn [{:keys [db]} [_ after-config-load]]
   (infof "loading config dispatch-after-load: %s"  after-config-load)
   (let [static? (= :static @webly-mode-atom)
         uri (if static?
               (str @entry-path-atom "/config.edn") ; (static-config-url)
               "/api/config")
         format (if static? 
                  (ajax/text-response-format)
                  (ajax/transit-response-format :json decode)
                  )]
     (info "loading config from url: " uri)
     {:db   (assoc-in db [:pref] (pref))
      :http-xhrio {:method          :get
                   :uri             uri
                   :timeout         10000                     ;; optional see API docs
                   :response-format format ;; IMPORTANT!: You must provide this.
                   :on-success      [:config/load-success after-config-load]
                   :on-failure      [:config/load-error]}})))


;(def config
;  {'bidi.bidi.TaggedMatch bidi/map->TaggedMatch})


;(defn read-str [s]
;  (read-string
;   {:readers config} s))

(reg-event-fx
 :config/load-success
 (fn [cofx [_ after-config-load config]]
   (let [static? (= :static @webly-mode-atom)
         config (if static? 
                   (cljs.reader/read-string config)
                  config)
         fx {:db          (assoc-in (:db cofx) [:config] config)
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
     (add-notification :error "Error loading config")
     (error "config load error: " details)
     db)))
