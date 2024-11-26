(ns demo.page.main.ws
  (:require
   [taoensso.timbre :refer-macros [warn]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [modular.ws.msg-handler :refer [-event-msg-handler]]
   [demo.helper.ui :refer [block2]]))

; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(def time-a (r/atom "no time"))
(def time2-a (r/atom "no time"))

(defmethod -event-msg-handler :demo/time
  [{:keys [?data]}]
  (reset! time-a ?data))

(defmethod -event-msg-handler :demo/time2
  [{:keys [?data]}]
  (reset! time2-a ?data))

(defn demo-ws []
  (let [c (rf/subscribe [:ws/connected?])]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: "  @time-a)]
       [:p (str "time as date: " @time2-a)]])))
