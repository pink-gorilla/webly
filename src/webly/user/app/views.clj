(ns webly.user.app.views
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [webly.config :refer [config-atom]]
   [webly.user.analytics.google-tag :refer [script-tag-src script-tag-config]]
   [webly.user.tenx.view :refer [tenx-script]]))

;; CSS

(defn css [link]
  [:link {:rel "stylesheet"
          :type "text/css"
          :href link}])

(defn style [s]
  (str/join ";" (map #(str (name %) ":" ((keyword %) s)) (keys s))))

(def body-loading-style
  [:style "body.loading {
              background: url(/r/webly/loading-lemur.jpg) no-repeat center center fixed;
              -webkit-background-size: cover;
              -moz-background-size: cover;
              -o-background-size: cover;
              background-size: cover;
            }"])

(def loading
  [:div
   [:script
    "window.onload = function () {
        document.getElementById('spinner').remove();
        var bodyClasses = document.body.classList;
        if (bodyClasses.contains('loading')) {
           bodyClasses.remove('loading');
        }
     }"]
   [:img {:id "spinner"
          :src "/r/webly/loading.svg"
          :style (style {:width "120px"
                         :height "120px"
                         :position "absolute"
                         :left "50%"
                         :top "50%"
                         :margin "-60px 0 0 -60px"})}]])

;; APP


(defn head [title icon css-extern google-analytics]
  [:head
   [:meta {:http-equiv "Content-Type"
           :content "text/html; charset=utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0"}]
   [:title title]
   [:link {:rel "shortcut icon" :href icon}]

   (tenx-script)

   (script-tag-src google-analytics)
   (script-tag-config google-analytics)

   ; css
   ;(css "/r/tailwindcss/dist/tailwind.css") (frontend side)

   ; fonts
   ;(css "http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic")
   ;(css "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic")
   ;(css "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300")
   ;(css "https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css")
   (doall (map css css-extern))

   body-loading-style])

(defn layout [webly-config page]
  (let [{:keys [title bundle-entry icon css-extern google-analytics]} webly-config]
    (page/html5
     (head title icon css-extern google-analytics)
     [:body.loading
      loading
      [:div#webly page]
      [:div  ; .w-screen.h-screen
       [:script {:src "/r/main.js"
                 :type "text/javascript"
                 :onload bundle-entry}]]])))

(defn app-page [csrf-token]
  (layout @config-atom
          [:div
           [:div#sente-csrf-token {:data-csrf-token csrf-token}]
           [:div#app]]))




