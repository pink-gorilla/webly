(ns demo.views
  (:require
   [re-frame.core :refer [dispatch]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [goto! current query-params]]
   [webly.user.notifications.core :refer [add-notification]]))

(defn show-dialog-demo []
  (dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
             :small]))

(defn main []
  [:div
   [:h1 "webly demo! "]
   [:ol
    [:li [:a.bg-green-300 {:on-click #(goto! :demo/help)} "help!"]]
    [:li [:a.bg-red-300 {:on-click #(goto! :demo/save)} "save-as (test for not implemented)"]]

    [:li [:p {:on-click #(add-notification "welcome to wonderland")} "show notification"]]
    [:li [:p {:on-click #(add-notification :danger "something bad happened")} "show notification - error"]]

    [:li [:p {:on-click show-dialog-demo} "show dialog"]]

    [:li [:a.bg-blue-300 {:href "/api/time"} "api time"]]
    [:li [:a.bg-blue-300 {:on-click #(dispatch [:oauth2/open-window :github])} "github login via popup (needs creds.edn)"]]
    [:li [:a.bg-blue-300 {:href "/oauth2/github/auth"} "github login via page-redirect (needs creds.edn)"]]]])

(defmethod reagent-page :demo/main [& args]
  [main])

(defn help []
  [:div
   [:h1 "webly help"]
   [:a.bg-green-300 {:on-click #(goto! :demo/main)} "main"]
   [:h1 "help!"]
   [:p "a moon image should show below. this is a test for webly resource handler."]
   [:img {:src "/r/moon.jpg"}]])

(defmethod reagent-page :demo/help [& args]
  [help])














