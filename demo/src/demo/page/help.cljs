(ns demo.page.help
  (:require
   [webly.spa.mode :refer [get-resource-path]]
   [demo.helper.ui :refer [link]]))

(defn help-page [match]
  (println "help page parameters: " (:parameters match))
  (let [url-moon (str (get-resource-path) "demo/moon.jpg")]
    [:div
     [:h1 "webly help"]
     [:p [:a {:href "/#/"} "main"]]
     [:h1 "help!"]
     [:p "a moon image should show below. this is a test for webly resource handler."]
     [:p "moon url: " url-moon]
     [:img {:src url-moon}]]))


