(ns webly.ws.events
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [reg-event-db reg-event-fx reg-sub dispatch]]
   ;[webly.user.notifications.core :refer [add-notification]]
   [webly.ws.core :refer [init-ws!]]))

(reg-event-db
 :ws/init
 (fn [db [_]]
   (let [url "/api/chsk"]
     (info "ws init: ")
     (init-ws! url)
     db)))

; [{:type :auto, :open? false, :ever-opened? false, :csrf-token "6zxUoCmfhv5lleMLfrHMgpChTHYecrY2TSswTz9YTrLLmm/bn7WWT+NCe4mbEFFfEg+gl/Zyobr7tdQX"} 
; {:type :ws, :open? true, :ever-opened? true, :csrf-token "6zxUoCmfhv5lleMLfrHMgpChTHYecrY2TSswTz9YTrLLmm/bn7WWT+NCe4mbEFFfEg+gl/Zyobr7tdQX", :uid "93732cb7-da5c-4792-88cd-c9362dbed11d", :handshake-data nil, :first-open? true}]

;[{:type :auto, 
  ;:open? false, 
  ;:ever-opened? false, 
  ;:csrf-token "2rSXN8g5Pd/ctm/JHj9F4eH85rynsgTj3H69O0mpxi2R/oLnwMFRgEoMGGEkUA09g+6VSFDvEYwXtn2/", 
  ;:last-ws-error {:udt 1618194904283, :ev #object[Event [object Event]]}, :last-close {:udt 1618194904289, :reason :downgrading-ws-to-ajax}, 
  ;:last-ws-close {:udt 1618194904303, :ev #object[CloseEvent [object CloseEvent]], :clean? false, :code 1006, :reason ""}, 
  ;:udt-next-reconnect 1618195624274} 

(reg-event-db
 :ws/state
 (fn [db [_ new-state-map old-state-map]]
   (debug "ws/state " new-state-map)
   (info "ws/ws state: " new-state-map)
   (when (:first-open? new-state-map)
     (info "ws open (first-time): " new-state-map))
   (assoc db :ws new-state-map)))

(reg-sub
 :ws/connected?
 (fn [db _]
   (get-in db [:ws :open?])))

(reg-event-db
 :ws/pong
 (fn [db [_ data]]
   (debug "ws pong: " data)
   db))

(reg-event-db
 :ws/unknown
 (fn [db [_ data]]
   (error "ws server does not know how to handle events of type: " data " you need to add code to clj side of your app")
   db))

