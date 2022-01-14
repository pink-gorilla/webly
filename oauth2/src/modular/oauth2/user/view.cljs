(ns modular.oauth2.user.view
  (:require
   [re-frame.core :as rf]
   [frontend.tooltip :refer [with-tooltip]]))

(defn header-ico [fa-icon rf-dispatch]
  [:a {:on-click #(rf/dispatch rf-dispatch)
       :class "hover:text-blue-700"}
   [:i {:class (str "fa-lg pl-1 " fa-icon)}]])

(defn header-icon [fa-icon rf-dispatch text]
  [with-tooltip text
   [header-ico fa-icon rf-dispatch]])

(defn user-login []
  (let [user (rf/subscribe [:oauth2/user])]
    (fn []
      (if @user
        [:span.text-green-800 (str "user: " @user)]
        [:span.text-red-500
         [header-icon "fas fa-user" [:login/dialog] "log in"]]))))

