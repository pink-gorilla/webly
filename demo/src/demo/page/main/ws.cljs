(ns demo.page.main.ws
  (:require
   [taoensso.timbre :refer-macros [warn]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [demo.helper.ui :refer [block2]]))

; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(defn demo-ws []
  (let [t (rf/subscribe [:demo/time])
        c (rf/subscribe [:ws/connected?])
        t2  (rf/subscribe [:demo/time2])]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @t2 (str @t2))]])))
