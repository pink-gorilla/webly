(ns demo.pages.main.ws
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [modular.oauth2.view :refer [tokens-view user-button]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(defn demo-ws []
  (let [t (rf/subscribe [:demo/time])
        c (rf/subscribe [:ws/connected?])
        tdt (r/atom nil)
        set-time-date (fn [[t v]] (reset! tdt v))]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @tdt (str @tdt))]
       [:div.flex.flex-col
        [link-fn #(rf/dispatch [:ws/send [:time/now []]]) " request time"]
        [link-fn #(rf/dispatch [:ws/send [:time/now-date []] set-time-date 5000]) " request time (as date)"]
        [link-fn #(rf/dispatch [:ws/send [:time/now-date-local []] set-time-date 5000]) " request time (as date local)"]
        [link-fn #(rf/dispatch [:ws/send [:demo/connected []] print-status 5000]) "request connections (see log)"]
        [link-fn #(rf/dispatch [:ws/send [:demo/xxx [123 456 789]]]) "no-server-handler (see log)"]
        
        ]])))
