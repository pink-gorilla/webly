(ns modular.oauth2.user.login-dialog
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [frontend.notifications.core :refer [add-notification]]))

(defn login-ui []
  (let [username (r/atom "")
        password (r/atom "")
        on-change (fn [a]
                    (fn [e]
                      (let [v (-> e .-target .-value)]
                        (info "changed: " v)
                        (reset! a v))))
        login-local (fn []
                      (info "logging in locally..")
                      (rf/dispatch [:ws/send
                                    [:login/local
                                     {:username @username :password @password}]]))
        login-oidc (fn [provider]
                     (info "logging in oidc " provider " ..")
                     (rf/dispatch [:oauth2/authorize-start provider :oauth2/login-oauth-success]))]

    (fn []
      [:div {:class "w-full flex items-center justify-center bg-blue-800"}
       [:div {:class "bg-gray-200 w-96 h-auto rounded-lg pt-8 pb-8 px-8 flex flex-col items-center"}

      ;; header
        [:label {:class "font-light text-4xl mb-4"} "gorilla"
         [:span {:class "font-bold"} "login"]]

      ; username
        [:input {:type "text"
                 :class "w-full h-12 rounded-lg px-4 text-lg focus:ring-blue-600 mb-4"
                 :placeholder "Email"
                 :value @username
                 :on-change (on-change username)}]
        [:input {:type "password"
                 :class "w-full h-12 rounded-lg px-4 text-lg focus:ring-blue-600 mb-4"
                 :placeholder "Password"
                 :value @password
                 :on-change (on-change password)}]
        [:button {:class "w-full h-12 rounded-lg bg-blue-600 text-gray-200 uppercase font-semibold hover:bg-blue-700 text-gray-100 transition mb-4"
                  :on-click login-local} "Login"]

        [:label {:class "text-gray-800 mb-4"} "or"]

        [:button {:class "w-full h-12 rounded-lg bg-red-600 text-gray-200 uppercase font-semibold hover:bg-red-700 text-gray-100 transition mb-4"
                  :on-click #(login-oidc :google)}
         "Sign with Google"]
        ;[:button {:class "w-full h-12 rounded-lg bg-blue-600 text-gray-200 uppercase font-semibold hover:bg-blue-700 text-gray-100 transition mb-4"}
        ; "Sign with Facebook"]
        ;[:button {:class "w-full h-12 rounded-lg bg-gray-800 text-gray-200 uppercase font-semibold hover:bg-gray-900 text-gray-100 transition mb-4"}
        ;"Sign with Github"]
        ]])))

(defn show-login-dialog []
  (rf/dispatch [:modal/open [:div [login-ui]]
                :medium]))

(rf/reg-event-fx :login/dialog
                 (fn [_ _]
                   (info "showing login dialog")
                   (show-login-dialog)))

(rf/reg-event-db
 :login/local
 (fn [db [_ {:keys [error error-message user token] :as result}]]
   (info "login result: " result)
   (rf/dispatch [:modal/close])
   (when error
     (add-notification :error error-message))
   (when user
     (add-notification :info (str "Logged in as: " user)))
   (if user
     (assoc db :user result)
     db)))

(rf/reg-event-fx
 :oauth2/login-oauth-success
 (fn [{:keys [db]} [_ provider token]]
   (info "oauth2 login success via oidc for provider " provider "token: " (pr-str token))
   (rf/dispatch [:ws/send [:login/oidc token]])
   nil))

;; LOGOUT

(rf/reg-event-fx
 :oauth2/logout
 (fn [{:keys [db]} [_ service]]
   (let [new-db (update-in db [:token] dissoc service)]
     {:db       new-db})))

