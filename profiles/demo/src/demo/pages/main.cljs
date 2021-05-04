(ns demo.pages.main
  (:require
    [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.user.notifications.core :refer [add-notification]]
   [webly.user.oauth2.view :refer [tokens-view user-button]]
   [webly.user.settings.local-storage :refer [ls-get ls-set!]]
   [webly.user.emoji :refer [emoji]]))

(defn show-dialog-demo []
  (dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
             :small]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn block [& children]
  (into [:div.bg-blue-400.m-5.inline-block {:class "w-1/4"}]
        children))


(defn demo-routing []
  [block
   [:p.text-4xl "bidi routes"]
   [:p [link-dispatch [:bidi/goto :demo/help] "help!"]]
   [:p [link-dispatch [:bidi/goto :demo/save-non-existing] "save-as (test for not implemented)"]]
   [:p [link-dispatch [:bidi/goto :demo/party :location "Vienna"] "party in vienna (test for route-params)"]]
   [:p [link-dispatch [:bidi/goto :demo/party :location "Bali" :query-params {:expected-guests 299}] "party in Bali (test for query-params)"]]

   [:p [link-href "/api/test" "demo api test"]]
   [:p [link-href "/api/time" "demo api time"]]])

(defn demo-oauth []
  [block
   [:p.text-4xl "oauth2"]
   [user-button :github]
   [user-button :google]
   [:p [link-dispatch [:oauth2/login :github] "github login via popup"]]
   [:p [link-dispatch [:oauth2/login :google] "google login via popup"]]
   [:p [link-href "/oauth2/github/token?code=99" "api test: github code ->token"]]
    ; [:p [:a.bg-blue-300 {:href "/oauth2/github/auth"} "github login via page-redirect (needs creds.edn)"]]

   [tokens-view]])

(defn demo-dialog []
  [block
   [:p.text-4xl "dialog"]
   [:ol
    [:li [link-fn #(add-notification "welcome to wonderland") "show notification"]]
    [:li [link-fn #(add-notification :danger "something bad happened") "show notification - error"]]
    [:li [link-fn show-dialog-demo "show dialog"]]]])


(defn demo-settings []
  (let [s (subscribe [:settings])]
    (fn []
      [block
       [:p.text-4xl "settings"]
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(dispatch [:settings/set :bongo 456]) " set bongo to 456"]])))


(defn demo-ws []
  (let [t (subscribe [:demo/time])
        c (subscribe [:ws/connected?])]
    (fn []
      [block
       [:p.text-4xl "websocket"]
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [link-fn #(dispatch [:ws/send [:demo/xxx [123 456 789]]]) " send unimplemented ws event request"]])))

(defn demo-kb []
  [block
   [:p.text-4xl "keybindings"]
   [:p "press [alt-g k] to see keybindings"]
   [:p "press [alt-g t] to toggle 10x"]])


(defn demo-emoji []
  (let [show (r/atom false)
        ; this triggers loading of the emojii css
        show! (fn []
                (warn "showing emojii")
                (dispatch [:css/set-theme-component :emoji true])
                (reset! show true))]
    (fn []
      [block
       [:p.text-4xl "css loader"]
       [link-fn show! "show emoji"]
       (when @show
         [emoji "fiem-surprised"])])))


(defn main []
  [:div
   [:h1 "webly demo"]

   [:p [link-dispatch [:bidi/goto :ui/markdown :file "webly.md"] "webly docs"]]
   [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]
   
   [demo-emoji]
   [demo-routing]
   [demo-dialog]
   [demo-oauth]
   [demo-settings]
   [demo-ws]
   [demo-kb]])

(defmethod reagent-page :demo/main [& args]
  [main])















