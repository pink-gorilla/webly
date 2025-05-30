(ns demo.page.main
  (:require
   ; webly
   [webly.spa.mode :refer [get-mode]]
   ; demo
   [demo.page.main.lazy :refer [demo-lazy]]
   [demo.page.main.route :refer [demo-routing]]
   [demo.page.main.css :refer [demo-css]]
   [demo.page.main.ws :refer [demo-ws]]
   [demo.page.main.dialog :refer [demo-dialog]]
   [demo.page.main.keybinding :refer [demo-keybinding]]))

(defn main-page [match]
  (println "main page parameters: " (:parameters match))
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
    (when (= :dynamic (get-mode))
      [demo-ws])
    [demo-lazy]
    [demo-dialog]
    [demo-keybinding]]])

















