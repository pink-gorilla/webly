(ns webly.user.oauth2.view
  (:require
   [cemerick.url :as url]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]
   [webly.user.oauth2.events] ; side-effects
   [webly.user.oauth2.subscriptions] ; side-effects
   ))

;; stolen from:
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/views/foursquare.cljs

(defn big-button
  [{:keys [dispatch class icon] :or {class "is-primary"}} label]
  [:div.columns
   [:div.column.is-half.is-offset-one-quarter
    [:a.button.is-medium {:on-click #(rf/dispatch dispatch)
                          :class class}
     [:span.icon
      [:i.fa {:class icon}]]
     [:span label]]]])

(defn- login-button [service]
  [big-button
   {:dispatch [:auth/login service]
    :icon "fa-foursquare"}
   "Log In to Foursquare"])

(defn- logout-button []
  [big-button
   {:dispatch [:auth/logout :foursquare]
    :icon "fa-foursquare"
    :class "is-warning"}
   "Log Out of Foursquare"])

(defn foursquare-page []
  (let [logged-in (rf/subscribe [:auth/logged-in? :foursquare])]
    (fn []
      (if @logged-in
        [logout-button]
        [login-button]))))

(defn hello-page
  "This page is the entry point into hr when the user returns from foursquare authentication."
  []
  (rf/dispatch [:auth/parse-token :foursquare])
  (fn []
    [:div.container.content ""]))

(defn auth-login []
  [:span.bg-red-700.pt-2.p-2
   [:a {:class "block mt-4 lg:inline-block lg:mt-0 text-pink-600 hover:bg-orange-500 mr-4"
        :on-click #(do
                     (rf/dispatch [:oauth2/open-window :github])
                     (rf/dispatch [:auth/login :foursquare])
                      ;(rf/dispatch [::events/set-navbar-menu-active? false])
                     )}
    "Github login"]])

(defn tokens-view []
  (let [tokens (rf/subscribe [:oauth2/tokens])]
    (fn []
      [:div.bg-orange-200
       [:p "oauth2 tokens"]
       [:p (pr-str @tokens)]])))

