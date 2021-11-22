(ns demo.pages.main
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [frontend.page :refer [reagent-page]]
   ; webly
   [demo.pages.main.lazy :refer [demo-lazy]]
   [demo.pages.main.route :refer [demo-routing]]
   [demo.pages.main.css :refer [demo-css]]
   [demo.pages.main.ws :refer [demo-ws]]
   [demo.pages.main.oauth :refer [demo-oauth]]
   ; frontend
   [demo.pages.main.settings :refer [demo-settings]]
   [demo.pages.main.dialog :refer [demo-dialog]]
   [demo.pages.main.keybinding :refer [demo-keybinding]]
   ))

(defn main []
  [:div.dark
   [:div {:class (str
                  "bg-gray-300 text-gray-900 "
                  "dark:bg-gray-800 dark:text-white "
                  "grid gap-4 " 
                  "grid-cols-1 "
                  "md:grid-cols-2 "
                  "lg:grid-cols-3 "
                  "xl:grid-cols-4 ")}
    [demo-lazy]
    [demo-routing]
    [demo-ws]
    [demo-css]
    [demo-oauth]
    [demo-dialog]
    [demo-settings]
    [demo-keybinding]
    ]])

(defmethod reagent-page :demo/main [& args]
  [main])















