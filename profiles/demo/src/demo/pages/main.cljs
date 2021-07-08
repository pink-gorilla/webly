(ns demo.pages.main
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]
   [webly.web.handler :refer [reagent-page]]
   [webly.user.notifications.core :refer [add-notification]]
   [webly.user.oauth2.view :refer [tokens-view user-button]]
   [webly.user.app.views :refer [refresh-page]]
   [webly.user.settings.local-storage :refer [ls-get ls-set!]]
   [webly.user.emoji :refer [emoji]]
   [webly.build.lazy :refer-macros [wrap-lazy] :refer [available]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))


;; BIDI ROUTING

(defn demo-routing []
  [block2 "bidi routes"
   [:div.flex.flex-col
    [link-dispatch [:bidi/goto "/help"]
     "help! (as an url)"]
    [link-dispatch [:bidi/goto "https://google.com"]
     "google"]
    [link-dispatch [:bidi/goto :demo/help]
     "help! (map with optional args))"]

    [link-dispatch [:bidi/goto :demo/save-non-existing]
     "save-as (test for not implemented)"]
    [link-dispatch [:bidi/goto :demo/party :location "Vienna"]
     "party in vienna (test for route-params)"]
    [link-dispatch [:bidi/goto :demo/party :location "Bali" :query-params {:expected-guests 299}]
     "party in Bali (test for query-params)"]
    [link-dispatch [:bidi/goto-route {:handler :demo/party
                                      :route-params {:location "Panama"}
                                      :query-params {:expected-guests 44}
                                      :tag nil}]
     "party in Panama (test for goto-route)"]

    [link-dispatch [:bidi/goto "/job"]
     "job! (test of bidi tags)"]
    [link-dispatch [:bidi/goto "/job2"]
     "job2! (test of bidi tags)"]

    [link-href "/api/test" "demo api test"]
    [link-href "/api/time" "demo api time"]
    [link-fn refresh-page "refresh page"]]])

;; OAUTH
(defn demo-oauth []
  [block2 "oauth2"
   [user-button :github]
   [user-button :google]
   [:p [link-dispatch [:oauth2/login :github] "github login via popup"]]
   [:p [link-dispatch [:oauth2/login :google] "google login via popup"]]
   [:p [link-href "/oauth2/github/token?code=99" "api test: github code ->token"]]
    ; [:p [:a.bg-blue-300 {:href "/oauth2/github/auth"} "github login via page-redirect (needs creds.edn)"]]

   [tokens-view]])

;; DIALOG
(def compile-error
  [:span.bg-green-600.m-3
   [:p.text-red-900 "compile error:" [:b "symbol banana not known"]]
   [:p "line: 12 col: 28"]])

(defn show-dialog-demo []
  (dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
             :small]))

(defn demo-dialog []
  [block2 "dialog"
   [:ol
    [:li [link-fn #(add-notification "welcome to wonderland") "show notification"]]
    [:li [link-fn #(add-notification :error "something bad happened") "show notification - error"]]
    [:li [link-fn #(add-notification :error compile-error 0) "show compile error"]]
    [:li [link-fn show-dialog-demo "show dialog"]]]])

(defn lazy1 []
  (let [ui-add (wrap-lazy snippets.snip/ui-add)]
    [:div
     [ui-add 7 7]
     ]))

(defn lazy2 []
  (let [ui-add-more (wrap-lazy snippets.snip/ui-add-more)]
    [:div
     [ui-add-more 7 7]]))

(defn demo-settings []
  (let [show-lazy1 (r/atom false)
        show-lazy2 (r/atom false)
        s (subscribe [:settings])]
    (fn []
      [block2 "settings"
       [:p (pr-str @s)]
       ;[link-fn #(ls-set! :webly {:willy 789}) "reset localstorage to :willy 789"]
       [link-fn #(dispatch [:settings/set :bongo 123]) " set bongo to 123"]
       [link-fn #(dispatch [:settings/set :bongo 456]) " set bongo to 456"]
       [link-fn #(reset! show-lazy1 true) "lazy load1"]
       [link-fn #(reset! show-lazy2 true) "lazy load2"]
       [:div "lazy renderer: " (pr-str (available))]
       (when @show-lazy1
         [lazy1])
       (when @show-lazy2
         [lazy2])
       [:p [link-dispatch [:reframe10x-toggle] "tenx-toggle"]]])))


; WEBSOCKET
(defn print-status [x]
  (warn "status: " x))

(defn demo-ws []
  (let [t (subscribe [:demo/time])
        c (subscribe [:ws/connected?])
        tdt (r/atom nil)
        set-time-date (fn [[t v]] (reset! tdt v))]
    (fn []
      [block2 "websocket"
       [:p (str "connected:" (if @c @c "not connected"))]
       [:p (str "time: " (if @t @t " no time received :-("))]
       [:p "time as date: " (when @tdt (str @tdt))]
       [link-fn #(dispatch [:ws/send [:time/now []]]) " request time"]
       [link-fn #(dispatch [:ws/send [:ws/status []] print-status 5000]) " request ws status"]
       [link-fn #(dispatch [:ws/send [:demo/xxx [123 456 789]]]) " send unimplemented ws event request"]
       [link-fn #(dispatch [:ws/send [:time/now-date []] set-time-date 5000]) " request time (as date)"]
       [link-fn #(dispatch [:ws/send [:time/now-date-local []] set-time-date 5000]) " request time (as date local)"]])))

(defn demo-kb []
  [block2 "keybindings"
   [:p "press [alt-g k] to see keybindings"]
   [:p "press [alt-g t] to toggle 10x"]
   [:p "press [alt-g 1] to goto main"]
   [:p "press [alt-g 2] to goto party (vienna)"]
   [:p "press [alt-g 3] to goto help"]
   [:p "press [alt-g 4] to goto job"]
   [:p "press [alt-g 5] to goto party (bali)"]])


; CSS
(defn demo-css []
  (let [show (r/atom false)
        ; this triggers loading of the emojii css
        show! (fn []
                (warn "showing emojii")
                (dispatch [:css/set-theme-component :emoji true])
                (reset! show true))]
    (fn []
      [block2 "css loader"
       [:div.flex.flex-col
        [link-dispatch [:css/set-theme-component :tailwind :light] "tailwind light"]
        [link-dispatch [:css/set-theme-component :tailwind :dark] "tailwind dark"]
        [link-fn show! "show emoji"]
        [link-dispatch [:css/add-components
                        {:bad {true  ["non-existing.css"]}}
                        {:bad :true}]
         "add non existing css"]
        (if @show
          [emoji "fiem-surprised"]
          [:span "emoji not loaded"])]])))



(defn main []
  [:div.dark
   [:div {:class "dark:bg-gray-800 bg-yellow-300 text-gray-900 dark:text-white grid grid-cols-4"}
    [demo-css]
    [demo-routing]
    [demo-dialog]
    [demo-oauth]
    [demo-settings]
    [demo-ws]
    [demo-kb]]])

(defmethod reagent-page :demo/main [& args]
  [main])















