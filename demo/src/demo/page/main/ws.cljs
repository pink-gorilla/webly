(ns demo.page.main.ws
  (:require
   [taoensso.timbre :refer-macros [warn]]
   [promesa.core :as p]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [goldly.service.core :refer [clj]]
   [demo.helper.ui :refer [link-fn block2]]))

; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(defn clj->atom [a & args]
  (println "executing clj: " args)
  (let [data-p (apply clj args)]
    (-> data-p
        (p/then (fn [result]
                  (reset! a result)))
        (p/catch (fn [err]
                   (println "error clj req: " args " error: " err))))
    nil))

(defn demo-ws []
  (let [t (rf/subscribe [:demo/time])
        c (rf/subscribe [:ws/connected?])
        a (r/atom nil)
        exec (fn [fun]
               (clj->atom a fun))]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @a (str @a))]
       [:div.flex.flex-col
        [link-fn #(exec 'demo.time/time-now) " request time"]
        [:p "the next service is secured and needs logged in user. user:demo pwd: 1234"]
        [link-fn #(rf/dispatch [:login/dialog]) "login"]
        [link-fn #(exec 'demo.time/time-now-date) " request time (as date)"]
        [:p "date-local needs supervisor role, which demo does not have!"]
        [:p "user: boss pwd:1234  does have the supervisor role!"]
        [link-fn #(exec 'demo.time/time-now-date-local) " request time (as date local)"]
        [link-fn #(exec 'demo.time/xxx) "no-server-handler (see log)"]]])))
