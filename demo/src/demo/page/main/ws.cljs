(ns demo.page.main.ws
  (:require
   [taoensso.timbre :refer-macros [warn]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [demo.helper.ui :refer [link-fn block2]]))

; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(defn demo-ws []
  (let [t (rf/subscribe [:demo/time])
        c (rf/subscribe [:ws/connected?])
        a (r/atom nil)]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @a (str @a))]])))
