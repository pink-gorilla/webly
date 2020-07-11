(ns webly.web.views
  (:require
   [clojure.string]
   [hiccup.page :as page]
   [webly.config :refer [webly-config]]))

(defn css [link]
  [:link {:rel "stylesheet"
          :type "text/css"
          :href link}])

(defn load-fonts []
  [:div
   (css "http://fonts.googleapis.com/css?family=Arvo:400,700,400italic,700italic|Lora:400,700,400italic,700italic")
   (css "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic")
   (css "https://fonts.googleapis.com/css?family=Roboto+Condensed:400,300")
   (css "https://cdnjs.cloudflare.com/ajax/libs/material-design-iconic-font/2.2.0/css/material-design-iconic-font.min.css")])

(defn layout [page]
  (let [{:keys [title start icon]} @webly-config]
    (page/html5
     [:head
      [:meta {:http-equiv "Content-Type"
              :content "text/html; charset=utf-8"}]
      [:meta {:name "viewport"
              :content "width=device-width, initial-scale=1.0"}]
      [:title title]
      [:link {:rel "shortcut icon"
              :href icon}]
      (css "/r/tailwindcss/dist/tailwind.css")
      (load-fonts)]
     [:body
      [:div#webly page]
      [:div  ; .w-screen.h-screen
       [:script {:src "/r/main.js" :type "text/javascript"}]
       [:script {:type "text/javascript"} start]]])))

(defn app-page [csrf-token]
  (layout [:div
           [:div#sente-csrf-token {:data-csrf-token csrf-token}]
           [:div#app]]))




