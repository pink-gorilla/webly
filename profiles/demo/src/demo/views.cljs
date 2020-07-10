(ns demo.views
  (:require
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [goto! current query-params]]))

(defn main []
  [:div
   [:h1 "webly demo"]
   [:a.bg-green-300 {:on-click #(goto! :demo/help)} "help"]
   [:a.bg-red-300 {:href "/demo/save"} "save-as (not implemented)"]])

(defmethod reagent-page :demo/main [& args]
  [main])

(defn help []
  [:div
   [:h1 "webly help"]
   [:a.bg-green-300 {:on-click #(goto! :demo/main)} "main"]
   [:h1 "help!"]])

(defmethod reagent-page :demo/help [& args]
  [help])














