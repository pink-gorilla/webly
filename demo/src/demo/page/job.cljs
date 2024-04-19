(ns demo.page.job
  (:require
   [re-frame.core :as rf]))

; ui helper   

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn job-page [{:keys [route-params query-params handler tag] :as p}]
  [:div.party
   [link-dispatch [:bidi/goto 'demo.page.main/main-page] "main"]
   [:p "This is a test for bidi route/query parameters."]

   [:p "params: " (pr-str p)]])

