(ns modular.ws.events
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [cemerick.url :as url]
   [re-frame.core :as rf]
   [modular.ws.core :refer [init-ws!]]))

(defn application-url
  "gets the current url, as a map"
  []
  (url/url (-> js/window .-location .-href)))

(defn is-https? []
  (let [{:keys [protocol]} (application-url)]
    (= "https" protocol)))

(defn is-served-by-shadow? [db]
  (let [app-url (application-url)
        port (:port app-url)
        shadow-dev-port (get-in db [:config :shadow :dev-http :port])
        shadow-port (get-in db [:config :shadow :http :port])]
    (or (= port shadow-dev-port)
        (= port shadow-port))))

(defn changed-port [db]
  (when (is-served-by-shadow? db)
    (warn "the page is served by shadow which does not support sente websockets - connecting to jetty server..")
    (if (is-https?)
      (get-in db [:config :webly/web-server :ssl-port])
      (get-in db [:config :webly/web-server :port]))))

(rf/reg-event-db
 :ws/init
 (fn [db [_]]
   (let [port (changed-port db)]
     (warn "ws connect to port (nil=unchanged): " port)
     (init-ws! "/api/chsk" port)
     db)))

#_[{:type :auto
    :open? false
    :ever-opened? false
    :csrf-token "6zxUoCmfhv5lleMLfrHMgpChTHYecrY2TSswTz9YTrLLmm/bn7WWT+NCe4mbEFFfEg+gl/Zyobr7tdQX"}

   {:type :ws
    :open? true
    :ever-opened? true
    :csrf-token "6zxUoCmfhv5lleMLfrHMgpChTHYecrY2TSswTz9YTrLLmm/bn7WWT+NCe4mbEFFfEg+gl/Zyobr7tdQX"
    :uid "93732cb7-da5c-4792-88cd-c9362dbed11d"
    :handshake-data nil
    :first-open? true}]

;[{:type :auto, 
  ;:open? false, 
  ;:ever-opened? false, 
  ;:csrf-token "2rSXN8g5Pd/ctm/JHj9F4eH85rynsgTj3H69O0mpxi2R/oLnwMFRgEoMGGEkUA09g+6VSFDvEYwXtn2/", 
  ;:last-ws-error {:udt 1618194904283, :ev #object[Event [object Event]]}, :last-close {:udt 1618194904289, :reason :downgrading-ws-to-ajax}, 
  ;:last-ws-close {:udt 1618194904303, :ev #object[CloseEvent [object CloseEvent]], :clean? false, :code 1006, :reason ""}, 
  ;:udt-next-reconnect 1618195624274} 

(rf/reg-event-db
 :ws/state
 (fn [db [_ new-state-map old-state-map]]
   (debug "ws/state " new-state-map)
   (when (:first-open? new-state-map)
     (info "ws open (first-time): " new-state-map)
     (rf/dispatch [:ws/open-first new-state-map]))
   (assoc db :ws new-state-map)))

(rf/reg-sub
 :ws/connected?
 (fn [db _]
   (get-in db [:ws :open?])))

(rf/reg-event-db
 :ws/unknown
 (fn [db [_ data]]
   (error "ws server does not know how to handle events of type: " data " you need to add code to clj side of your app")
   db))

