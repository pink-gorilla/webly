(ns webly.user.markdown.events
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [reg-event-db reg-event-fx dispatch]]
   [day8.re-frame.http-fx] ; side-effects
   [ajax.core :as ajax] ; https://github.com/JulianBirch/cljs-ajax
   [webly.web.routes :refer [link]]))

(reg-event-db
 :markdown/init
 (fn [db [_]]
   (let [db (or db {})]
     (info "markdown init .. ")
     (assoc db :markdown {:available []
                          :showing {:file nil
                                    :doc nil}}))))


;; index


(reg-event-fx
 :markdown/load-index
 (fn [{:keys [db]} [_]]
   (let [url  "/api/md"
         _ (info "loading md index from :" url)]
     {:db         (assoc-in db [:markdown :available] [])
      :http-xhrio {:method          :get
                   :uri             url
                   ;:params          params
                   :timeout         15000
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:markdown/load-index-success]
                   :on-failure      [:markdown/load-index-error]}})))

(reg-event-db
 :markdown/load-index-error
 (fn
   [db [_ response-body]]
   (let [content (:content response-body)]
     (error ":markdown/load-index-error content: " content)
     (assoc-in db [:markdown :error-index] {:error response-body}))))

(reg-event-db
 :markdown/load-index-success
 (fn
   [db [_ available]]
   (assoc-in db [:markdown :available] (:data available))))

;; file

(reg-event-fx
 :markdown/load
 (fn [{:keys [db]} [_ filename]]
   (let [url  (str "/r/gorillamd/" filename)
         _ (info "loading md from :" url)]
     {:dispatch [:ga/event {:category "webly" :action "md-load" :label "md" :value filename}]
      :db   (-> db
                (assoc-in [:markdown :showing :file] filename)
                (assoc-in [:markdown :showing :doc] :markdown/loading)) ; notebook view on loading
      :http-xhrio {:method          :get
                   :uri             url
                   ;:params          params
                   :timeout         15000
                   :response-format (ajax/text-response-format)   ;(ajax/json-response-format {:keywords? true})
                   :on-success      [:markdown/load-success]
                   :on-failure      [:markdown/load-error]}})))

(reg-event-db
 :markdown/load-error
 (fn [db [_ response-body]]
   (let [content (:content response-body)
         _ (error "Content Only:\n" content)]
     (assoc-in db [:markdown :showing :doc]
               {:error response-body}))))

(reg-event-db
 :markdown/load-success
 (fn
   [db [_ md]]
   (assoc-in db [:markdown :showing :doc] md)))