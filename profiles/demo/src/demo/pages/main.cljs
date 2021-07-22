(ns demo.pages.main
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [webly.web.handler :refer [reagent-page]]
   ; boxes
   [demo.pages.main.css :refer [demo-css]]
   [demo.pages.main.bidi :refer [demo-routing]]
   [demo.pages.main.dialog :refer [demo-dialog]]
   [demo.pages.main.oauth :refer [demo-oauth]]
   [demo.pages.main.ws :refer [demo-ws]]
   [demo.pages.main.kb :refer [demo-kb]]
   [demo.pages.main.settings :refer [demo-settings]]))

(defn main []
  [:div.dark
   [:div {:class "dark:bg-gray-800 bg-yellow-300 text-gray-900 dark:text-white grid grid-cols-4"}
    [demo-css]
    [demo-routing]
    [demo-dialog]
    [demo-oauth]
    [demo-settings]
    [demo-ws]
    [demo-kb]]])

(defmethod reagent-page :demo/main [& args]
  [main])















