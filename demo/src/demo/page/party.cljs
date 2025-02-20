(ns demo.page.party
  (:require
   [reagent.core :as r]
   [demo.helper.ui :refer [link-dispatch link-fn]]
   [frontend.css :refer [set-theme-component add-components]]))

; themeable css for party

(def components
  {:party {:blue  ["demo/party-theme-blue.css"]
           :red ["demo/party-theme-red.css"]
           :cool ["demo/party-theme-cool.css"]}})

(def config
  {:party :red})

(defn party [_location _expected-guests]
  (let [first (r/atom true)]
    (fn [location expected-guests]
      (when @first
        (add-components {:available components
                         :current config})
        (reset! first false))
      [:div.party
       [link-dispatch [:bidi/goto 'demo.page.main/main-page] "main"]
       [:p "This is a test for bidi route/query parameters."]

       [:p "This is a test for theme css switching."]
       [link-fn #(set-theme-component :party :red) "theme red"]
       [link-fn #(set-theme-component :party :blue) "theme blue"]
       [link-fn #(set-theme-component :party :cool) "theme cool"]

       [:h1.text-3xl.text-blue-500 "There is a party in " location]
       (if expected-guests
         [:p "Expected Guests: " expected-guests]
         [:p "We don't know how many guests to expect!"])

       [:a {:href "/party/kabul"}
        [:p.bg-red-400.m-3 "secret party"]]])))

(defn party-page [{:keys [_handler route-params query-params]}]
  (let [{:keys [location]} route-params
        {:keys [expected-guests]} query-params]
    [party location expected-guests]))