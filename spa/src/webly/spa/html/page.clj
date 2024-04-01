(ns webly.spa.html.page
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [taoensso.timbre :refer [debug info error]]
   [frontend.analytics.google-tag :refer [script-tag-src script-tag-config]]
   [frontend.css.config :refer [css-app]]
   [webly.app.tenx.view :refer [tenx-script]]))

;; CSS

(defn css-link [link]
  [:link {:class "webly"
          :rel "stylesheet"
          :type "text/css"
          :href link}])

(defn css->hiccup [prefix theme]
  (let [{:keys [available current]} theme
        css-links (css-app prefix available current)]
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

(defn loading [spinner-src]
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
          :src spinner-src
          :style (style {:width "120px"
                         :height "120px"
                         :position "absolute"
                         :left "50%"
                         :top "50%"
                         :margin "-60px 0 0 -60px"})}]])

;; APP


(defn head [{:keys [title loading-image-url icon] :as _spa} 
            theme
            prefix
            google-analytics]
  (let [head [:head
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
              [:link {:rel "shortcut icon" :href (str prefix icon)}]
              (tenx-script)
              (body-loading-style (str prefix loading-image-url))
              (script-tag-src google-analytics)
              (script-tag-config google-analytics)]]
    (into head
          (css->hiccup prefix theme))))

(defn layout [{:keys [spinner] :as spa} 
              theme 
              prefix
              google-analytics
              page]
    (page/html5
     {:mode :html}
     (head spa theme prefix google-analytics)
     [:body.loading
      (loading (str prefix spinner))
      [:div#webly page]]))

(defn app-page-dynamic [frontend-config csrf-token]
   (let [{:keys [spa theme prefix google-analytics]} frontend-config]
  (layout spa theme prefix google-analytics
          [:div
           [:div#sente-csrf-token {:data-csrf-token csrf-token}]
           [:div#app]
           [:div  ; .w-screen.h-screen
            [:script {:src (str prefix "webly.js")
                      :type "text/javascript"
                      :onload "webly.app.app.start ('dynamic');"}]]])))

(defn config-prefix-adjust [prefix static-main-path]
  (let [asset-path (str static-main-path prefix)]
    (info "static asset path: " asset-path)
    asset-path))

(defn app-page-static [frontend-config csrf-token]
  (let [{:keys [spa theme prefix google-analytics]} frontend-config]
    (layout spa theme prefix google-analytics ; :prefix "/r/"
            [:div
             [:div#sente-csrf-token {:data-csrf-token csrf-token}]
             [:div#app]
             [:div  ; .w-screen.h-screen
              [:script {:src (str prefix "webly.js")
                        :type "text/javascript"
                        :onload "webly.app.app.start ('static');"}]]])))







