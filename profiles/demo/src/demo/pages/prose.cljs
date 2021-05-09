(ns demo.pages.prose
  (:require
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn prose []
  [:div
   [:h1 "webly prose"]
   [:p [link-dispatch [:bidi/goto  :demo/main] "main"]]
   [:div.pl-8
    [:div.prose
     [:h1 "h1"]
     [:h2 "h2 header"]
     [:h3 "h1 header"]
     [:p "p paragraph (after: hr)"]
     [:hr]
     [:a {:href "/main"} [:p "p paragraph (with link)"]]
     [:ol
      [:li "list-item 1"]
      [:li "list-item 2"]]
     [:ul
      [:li "apple"]
      [:li "banana"]]
     [:blockquote "quote kj kj lk hkjl hjk kj"]
     [:code "(+ 7 7 7 7 7)"]
     ]]])

(defmethod reagent-page :demo/prose [& args]
  [prose])
