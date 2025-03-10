(ns demo.page.help
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.spa.mode :refer [get-resource-path]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn help-page [_route]
  (let [url-moon (str (get-resource-path) "demo/moon.jpg")]
    [:div
     [:h1 "webly help"]
     [:p [link-dispatch [:bidi/goto 'demo.page.main/main-page] "main"]]
     [:h1 "help!"]
     [:p "a moon image should show below. this is a test for webly resource handler."]
     [:p "moon url: " url-moon]
     [:img {:src url-moon}]]))


