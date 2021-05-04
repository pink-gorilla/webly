(ns demo.pages.party
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

(defn party [{:keys [route-params query-params handler]}]
  (let [{:keys [location]} route-params
        {:keys [expected-guests]} query-params]
    [:div
     [link-dispatch [:bidi/goto :demo/main] "main"]
     [:p "This is a test for bidi route/query parameters."]

     [:h1.text-3xl.text-blue-500 "There is a party in " location]
     (if expected-guests
       [:p "Expected Guests: " expected-guests]
       [:p "We don't know how many guests to expect!"])

     [:a {:href "/party/kabul"}
      [:p.bg-red-400.m-3 "best pary link ever!"]]]))

(defmethod reagent-page :demo/party [args]
  [party args])