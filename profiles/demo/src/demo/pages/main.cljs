(ns demo.pages.main
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [shadow.lazy :as lazy]
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
   [:p [link-dispatch [:bidi/goto "/help"] "help! (as an url)"]]
   [:p [link-dispatch [:bidi/goto "https://google.com"] "google"]]
   [:p [link-dispatch [:bidi/goto :demo/help] "help! (map with optional args))"]]

   [:p [link-dispatch [:bidi/goto :demo/save-non-existing] "save-as (test for not implemented)"]]
   [:p [link-dispatch [:bidi/goto :demo/party :location "Vienna"] "party in vienna (test for route-params)"]]
   [:p [link-dispatch [:bidi/goto :demo/party :location "Bali" :query-params {:expected-guests 299}] "party in Bali (test for query-params)"]]

   [:p [link-dispatch [:bidi/goto "/job"] "job! (test of bidi tags)"]]
   [:p [link-dispatch [:bidi/goto "/job2"] "job2! (test of bidi tags)"]]

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


(def x (lazy/loadable snippets.snip/add))

(defn handle-load [fun]
  (info "result: " (fun 2 7)))


(defn module-fun []
  (info "module fun..")
  ; https://code.thheller.com/blog/shadow-cljs/2019/03/03/code-splitting-clojurescript.html
  ; https://clojureverse.org/t/shadow-lazy-convenience-wrapper-for-shadow-loader-cljs-loader/3841
  (lazy/load x handle-load))


(defn demo-settings []
  (let [s (subscribe [:settings])]
    (fn []
      [block
       [:p.text-4xl "settings"]
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(dispatch [:settings/set :bongo 456]) " set bongo to 456"]
       [link-fn module-fun "call module fun"]])))


(defn print-status [x]
  (warn "status: " x))

(defn demo-ws []
  (let [t (subscribe [:demo/time])
        c (subscribe [:ws/connected?])
        tdt (r/atom nil)
        set-time-date (fn [[t v]] (reset! tdt v))]
    (fn []
      [block
       [:p.text-4xl "websocket"]
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @tdt (str @tdt))]
       [link-fn #(dispatch [:ws/send [:time/now []]]) " request time"]
       [link-fn #(dispatch [:ws/send [:ws/status []] print-status 5000]) " request ws status"]
       [link-fn #(dispatch [:ws/send [:demo/xxx [123 456 789]]]) " send unimplemented ws event request"]
       [link-fn #(dispatch [:ws/send [:time/now-date []] set-time-date 5000]) " request time (as date)"]
       [link-fn #(dispatch [:ws/send [:time/now-date-local []] set-time-date 5000]) " request time (as date local)"]])))

(defn demo-kb []
  [block
   [:p.text-4xl "keybindings"]
   [:p "press [alt-g k] to see keybindings"]
   [:p "press [alt-g t] to toggle 10x"]
   [:p "press [alt-g 1] to goto main"]
   [:p "press [alt-g 2] to goto party (vienna)"]
   [:p "press [alt-g 3] to goto help"]
   [:p "press [alt-g 4] to goto job"]
   [:p "press [alt-g 5] to goto party (bali)"]])


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
  [:div.dark
   [:div {:class "dark:bg-gray-800 bg-yellow-300 text-gray-900 dark:text-white"}
    [:h1 "webly demo"]

    [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]

    [link-dispatch [:css/set-theme-component :tailwind :light] "tailwind light"]
    [link-dispatch [:css/set-theme-component :tailwind :dark] "tailwind dark"]

    [demo-emoji]
    [demo-routing]
    [demo-dialog]
    [demo-oauth]
    [demo-settings]
    [demo-ws]
    [demo-kb]]])

(defmethod reagent-page :demo/main [& args]
  [main])















