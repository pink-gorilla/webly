(ns demo.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [goto! current query-params]]
   [webly.user.notifications.core :refer [add-notification]]
   [webly.user.oauth2.view :refer [tokens-view]]
   [webly.user.settings.local-storage :refer [ls-get ls-set!]]))

(defn show-dialog-demo []
  (dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
             :small]))


;(def g! goto!)


; g! does the same as goto! but dispatches reframe bidi/goto event
(defn g!
  ([handler]
   (println "dispatching bidi/goto (no-args): " handler)
   (dispatch [:bidi/goto handler]))
  ([handler & args]
   (println "g! with-args: handler:" handler "args: " args)
   (let [x (into [:bidi/goto handler] args)]
     (println "dispatching bidi/goto (args): " x)
     (dispatch x))))


(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])


(defn demo-routing []
  [:div.bg-blue-400.m-5 {:class "w-1/4"}
   [:p.text-4xl "bidi routes"]
   [:p [:a.bg-green-300 {:on-click #(g! :demo/help)} "help!"]]
   [:p [:a.bg-red-300 {:on-click #(g! :demo/save-non-existing)} "save-as (test for not implemented)"]]

   [:p [:a.bg-green-300 {:on-click #(g! :demo/party :location "Vienna")} "party in vienna (test for route-params)"]]
   [:p [:a.bg-green-300 {:on-click #(g! :demo/party :location "Bali" :query-params {:expected-guests 299})} "party in Bali (test for query-params)"]]

   [:p [link-href "/api/test" "demo api test"]]
   [:p [link-href "/api/time" "demo api time"]]])

(defn demo-oauth []
  [:div.bg-blue-400.m-5 {:class "w-1/4"}
   [:p.text-4xl "oauth2"]
   [:p [link-dispatch [:oauth2/login :github] "github login via popup"]]
   [:p [link-dispatch [:oauth2/login :google] "google login via popup"]]
   [:p [link-href "/oauth2/github/token?code=99" "api test: github code ->token"]]
    ; [:p [:a.bg-blue-300 {:href "/oauth2/github/auth"} "github login via page-redirect (needs creds.edn)"]]

   [tokens-view]])

(defn demo-dialog []
  [:div.bg-blue-400.m-5 {:class "w-1/4"}
   [:p.text-4xl "dialog"]
   [:ol
    [:li [link-fn #(add-notification "welcome to wonderland") "show notification"]]
    [:li [link-fn #(add-notification :danger "something bad happened") "show notification - error"]]
    [:li [link-fn show-dialog-demo "show dialog"]]]])


(defn demo-settings []
  (let [s (subscribe [:settings])]
    (fn []
      [:div.bg-blue-400.m-5 {:class "w-1/4"}
       [:p.text-4xl "settings"]
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(dispatch [:settings/set :bongo 456]) " set bongo to 456"]])))


(defn demo-ws []
  (let [t (subscribe [:demo/time])
        c (subscribe [:ws/connected?])
        ]
    (fn []
      [:div.bg-blue-400.m-5 {:class "w-1/4"}
       [:p.text-4xl "websocket"]
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [link-fn #(dispatch [:ws/send [:demo/xxx [123 456 789]]]) " send unimplemented ws event request"]
     ])))

(defn main []
  [:div
   [:h1 "webly demo"]

   [:p [link-dispatch [:bidi/goto :ui/markdown :file "webly.md"] "webly docs"]]
   [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]

   [demo-routing]
   [demo-dialog]
   [demo-oauth]
   [demo-settings]
   [demo-ws]
   ])

(defmethod reagent-page :demo/main [& args]
  [main])

(defn help []
  [:div
   [:h1 "webly help"]
   [:a.bg-green-300 {:on-click #(g! :demo/main)} "main"]
   [:h1 "help!"]
   [:p "a moon image should show below. this is a test for webly resource handler."]
   [:img {:src "/r/moon.jpg"}]])

(defmethod reagent-page :demo/help [& args]
  [help])


(defn party [{:keys [route-params query-params handler]}]
  (let [{:keys [location]} route-params
        {:keys [expected-guests]} query-params]
    [:div
     [:h1 "There is a party in " location]
     (if expected-guests
       [:p "Expected Guests: " expected-guests]
       [:p "We don't know how many guests to expect!"])
     [:p "This is a test for bidi route/query parameters."]]))

(defmethod reagent-page :demo/party [args]
  [party args])












