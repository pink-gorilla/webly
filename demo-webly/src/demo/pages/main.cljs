(ns demo.pages.main
  (:require
   [frontend.page :refer [add-page]]
   ; webly
   [demo.pages.main.lazy :refer [demo-lazy]]
   [demo.pages.main.route :refer [demo-routing]]
   [demo.pages.main.css :refer [demo-css]]
   [demo.pages.main.ws :refer [demo-ws]]
   ; frontend
   [demo.pages.main.settings :refer [demo-settings]]
   [demo.pages.main.dialog :refer [demo-dialog]]
   [demo.pages.main.keybinding :refer [demo-keybinding]]))

(defn main [_route]
  [:div.dark
   [:div {:class (str
                  "w-screen h-screen overflow-hidden m-0 p-0"
                  "bg-gray-300 text-gray-900 "
                  "dark:bg-gray-800 dark:text-white "
                  "grid gap-0 "
                  "grid-cols-1 "
                  "md:grid-cols-2 "
                  "lg:grid-cols-3 "
                  "xl:grid-cols-4 ")}
    [demo-routing]
    [demo-css]
    [demo-ws]

    [demo-lazy]
    [demo-dialog]
    [demo-settings]
    [demo-keybinding]]])

(add-page :demo/main main)















