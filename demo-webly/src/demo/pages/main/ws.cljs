(ns demo.pages.main.ws
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   ;[modular.oauth2.user.view :refer [tokens-view user-button]]
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
        [:p "the next service is secured and needs logged in user. user:demo pwd: 1234"]
        [link-fn #(rf/dispatch [:login/dialog]) "login"]
        [link-fn #(rf/dispatch [:ws/send [:time/now-date []] set-time-date 5000]) " request time (as date)"]
        [:p "date-local needs supervisor role, which demo does not have!"]
        [:p "user: boss pwd:1234  does have the supervisor role!"]
        [link-fn #(rf/dispatch [:ws/send [:time/now-date-local []] set-time-date 5000]) " request time (as date local)"]
        [link-fn #(rf/dispatch [:ws/send [:demo/connected []] print-status 5000]) "request connections (see log)"]
        [link-fn #(rf/dispatch [:ws/send [:demo/xxx [123 456 789]]]) "no-server-handler (see log)"]
        
        ]])))
