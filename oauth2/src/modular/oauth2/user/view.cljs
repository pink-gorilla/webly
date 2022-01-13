(ns modular.oauth2.user.view
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [frontend.tooltip :refer [with-tooltip]]))

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
  (let [logged-in (subscribe [:oauth2/logged-in? service])
        logged-in-email (subscribe [:oauth2/logged-in-email-or-user service])]
    (fn [service]
      (if @logged-in
        [:span.text-green-800 [header-icon (service-icon service)  [:oauth2/logout service] (str "log out: " @logged-in-email)]]
        [:span.text-red-500 [header-icon (service-icon service) [:oauth2/login service] "log in"]]))))



