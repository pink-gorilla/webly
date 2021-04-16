(ns webly.user.oauth2.view
  (:require
   [re-frame.core :as rf :refer [dispatch subscribe]]
   [webly.user.oauth2.events] ; side-effects
   [webly.user.oauth2.subscriptions] ; side-effects
   [webly.user.tooltip :refer [with-tooltip]]))

(defn header-ico [fa-icon rf-dispatch]
  [:a {:on-click #(dispatch rf-dispatch)
       :class "hover:text-blue-700 mr-1"}
   [:i {:class (str "fa-lg pl-1 " fa-icon)}]])

(defn header-icon [fa-icon rf-dispatch text]
  [with-tooltip text
   [header-ico fa-icon rf-dispatch]])

(defn service-icon [service]
  (case service
    :google "fab fa-google-plus"
    :github "fab fa-github-square"
    "fas fa-user"))

(defn user-button [service]
  (let [logged-in (rf/subscribe [:oauth2/logged-in? service])]
    (fn [service]
      (if @logged-in
        [:span.text-green-800 [header-icon (service-icon service)  [:oauth2/logout service] "log out"]]
        [:span.text-red-500 [header-icon (service-icon service) [:oauth2/login service] "log in"]]))))

(defn tokens-view []
  (let [tokens (rf/subscribe [:oauth2/tokens])]
    (fn []
      [:div.bg-orange-200
       [:p "oauth2 tokens"]
       [:p (pr-str @tokens)]])))

