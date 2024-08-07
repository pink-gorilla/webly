(ns demo.page.help
  (:require
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.spa.mode :refer [get-resource-path]]
   [pinkgorilla.repl.cljs.http :refer [get-json]]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(def data-iss (r/atom {:data nil}))
(def data-config (r/atom {:data nil}))
(def firstt (r/atom true))

(defn help-page [_route]
  (let [url-moon (str (get-resource-path) "demo/moon.jpg")
        url-config (str (get-resource-path) "config.edn")]
    [:div
     [:h1 "webly help"]
     (when @firstt
       (reset! firstt false)
       (get-json "http://api.open-notify.org/iss-now.json" data-iss [:data]) ;cors test
       (get-json url-config data-config [:data])
       nil)
     [:p [link-dispatch [:bidi/goto 'demo.page.main/main-page] "main"]]
     [:p "url config: " url-config]
     [:h1 "help!"]
     [:div.bg-blue-300 "iss data (cors test - there should be data coming!):" (pr-str @data-iss)]
     [:div.bg-red-300 "config data:" (pr-str @data-config)]

     [:p "a moon image should show below. this is a test for webly resource handler."]
     [:p "moon url: " url-moon]
     [:img {:src url-moon}]]))


