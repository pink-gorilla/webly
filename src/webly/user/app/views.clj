(ns webly.user.app.views
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [taoensso.timbre :refer [debug info error]]
   [webly.config :refer [config-atom]]
   [webly.user.analytics.google-tag :refer [script-tag-src script-tag-config]]
   [webly.user.tenx.view :refer [tenx-script]]
   [webly.user.css.config :refer [css-app]]))

;; CSS

(defn css-link [link]
  [:link {:class "webly"
          :rel "stylesheet"
          :type "text/css"
          :href link}])

(defn css->hiccup [webly-config]
  (let [theme (get-in webly-config [:webly :theme])
        {:keys [available current]} theme
        css-links (css-app available current)]
    (debug "css links: " css-links)
    (doall (map css-link css-links))))

;; loading spinner
(defn style [s]
  (str/join ";" (map #(str (name %) ":" ((keyword %) s)) (keys s))))

(defn body-loading-style [loading-image-url]
  [:style (str "body.loading {
              background: url(" loading-image-url ") no-repeat center center fixed;
              -webkit-background-size: cover;
              -moz-background-size: cover;
              -o-background-size: cover;
              background-size: cover;
            }")])

(def loading
  [:div
   #_[:script
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

(defn head [webly-config]
  (let [{:keys [webly google-analytics]} webly-config
        {:keys [title loading-image-url icon]} webly
        head [:head
              [:meta {:http-equiv "Content-Type"
                      :content "text/html; charset=utf-8"}]
              [:meta {:name "viewport"
                      :content "width=device-width, initial-scale=1.0"}]
              [:meta {:name "description"
                      :content "webly app"}]
              [:meta {:name "author"
                      :content "pink-gorilla"}]
              ; <meta name= "keywords" content= "keywords,here" >
              [:title title]
              [:link {:rel "shortcut icon" :href icon}]
              (tenx-script)
              (body-loading-style loading-image-url)
              (script-tag-src google-analytics)
              (script-tag-config google-analytics)]]
    (into head
          (css->hiccup webly-config))))

(defn layout [webly-config page]
  (let [{:keys [webly]} webly-config
        {:keys [webly-bundle-entry]} webly]
    (page/html5
     {:mode :html}
     (head webly-config)
     [:body.loading
      loading
      [:div#webly page]
      [:div  ; .w-screen.h-screen
       [:script {:src "/r/main.js"
                 :type "text/javascript"
                 :onload webly-bundle-entry}]]])))

(defn app-page [csrf-token]
  (layout @config-atom
          [:div
           [:div#sente-csrf-token {:data-csrf-token csrf-token}]
           [:div#app]]))




