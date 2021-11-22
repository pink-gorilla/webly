(ns demo.pages.help
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [frontend.page :refer [reagent-page]]
   [pinkgorilla.repl.cljs.http :refer [get-json]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(def data (r/atom {:d nil}))
(def firstt (r/atom true))

(defn help []
  [:div
   [:h1 "webly help"]
   (when @firstt
     (reset! firstt false)
     (get-json "http://api.open-notify.org/iss-now.json" data [:d]) ;cors test
     (get-json "/api/config" data [:config])
     nil)
   [:p [link-dispatch [:bidi/goto  :demo/main] "main"]]
   [:h1 "help!"]
   [:div "data:" (pr-str @data)]

   [:p "a moon image should show below. this is a test for webly resource handler."]
   [:img {:src "/r/moon.jpg"}]])

(defmethod reagent-page :demo/help [& args]
  [help])
