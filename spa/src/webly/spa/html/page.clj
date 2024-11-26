(ns webly.spa.html.page
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [taoensso.timbre :refer [debug info error]]
   [frontend.css.config :refer [css-app]]
   ;[frontend.analytics.google-tag :refer [script-tag-src script-tag-config]]
   ;[webly.spa.tenx.view :refer [tenx-script]]
   ))

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
            ;google-analytics
            ]
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
              ;(tenx-script)
              (body-loading-style (str prefix loading-image-url))
              ;(script-tag-src google-analytics)
              ;(script-tag-config google-analytics)
              ]]
    (into head
          (css->hiccup prefix theme))))

(defn layout [{:keys [spinner] :as spa}
              theme
              prefix
              #_google-analytics
              page]
  (page/html5
   {:mode :html}
   (head spa theme prefix #_google-analytics)
   [:body.loading
    (loading (str prefix spinner))
    [:div#webly page]
    [:div {; required by glide data grid.
           :id "portal"
           :style "position: fixed; left: 0; top: 0; z-index: 9999;"}]]))

(defn app-page-dynamic [frontend-config csrf-token]
  (let [{:keys [spa theme prefix]} frontend-config]
    (layout spa theme prefix
            [:div
             [:div#sente-csrf-token {:data-csrf-token csrf-token}]
             [:div#app]
             [:div
              [:script {:src (str prefix "init.js")
                        :type "text/javascript"
                        :onload "webly.init.start ('dynamic');"}]]])))

(defn config-prefix-adjust [prefix static-main-path]
  (let [asset-path (str static-main-path prefix)]
    (info "static asset path: " asset-path)
    asset-path))

(defn app-page-static [frontend-config csrf-token]
  (let [{:keys [spa theme prefix]} frontend-config]
    (info "static prefix: " prefix)
    (layout spa theme prefix ; :prefix "/r/"
            [:div
             [:div#sente-csrf-token {:data-csrf-token csrf-token}]
             [:div#app]
             [:div
              [:script {:src (str prefix "init.js")
                        :type "text/javascript"
                        :onload "webly.init.start ('static');"}]]])))







