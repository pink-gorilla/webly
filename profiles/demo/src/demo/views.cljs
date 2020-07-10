(ns demo.views
  (:require
   [webly.web.handler :refer [reagent-page]]
   [pinkgorilla.bidi.routes :refer [goto! current query-params]]))

(defn main []
  [:div
   [:h1 "webly demo"]
   [:a.bg-green-300 {:href "/explorer"} "explorer"]
   [:a.bg-red-300 {:href "/demo/save"} "save-as dialog demo"]])

(defmethod reagent-page :demo/main [& args]
  [main])














