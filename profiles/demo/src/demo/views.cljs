(ns demo.views
  (:require
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [goto! current query-params]]))

(defn main []
  [:div
   [:h1 "webly demo"]
   [:a.bg-green-300 {:on-click #(goto! :demo/help)} "help"]
   [:a.bg-red-300 {:on-click #(goto! :demo/save)} "save-as (not implemented)"]
   [:a.bg-blue-300 {:href "/api/time"} "api time"]])

(defmethod reagent-page :demo/main [& args]
  [main])

(defn help []
  [:div
   [:h1 "webly help"]
   [:a.bg-green-300 {:on-click #(goto! :demo/main)} "main"]
   [:h1 "help!"]
   [:p "a moon image should show below. this is a test for webly resource handler."]
   [:img {:src "/r/moon.jpg"}]])

(defmethod reagent-page :demo/help [& args]
  [help])














