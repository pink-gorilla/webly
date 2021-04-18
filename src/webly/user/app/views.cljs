(ns webly.user.app.views
  (:require
   [cljs.pprint]
   [reagent.dom]
   [re-frame.core :refer [subscribe]]
   [webly.config :refer-macros [get-in-config-cljs]]
   [webly.web.handler :refer [reagent-page]]
   [webly.web.routes :refer [current]]
   [webly.user.config.core :refer [link-css]]
   [webly.user.dialog :refer [modal-container]]
   [webly.user.notifications.dialog :refer [notifications-container]]))

(defn not-found-page []
  [:div.bg-red-500.m-5
   [:h1 "Bummer, reagent-handler not found!"]
   [:p "Current Page:" (str @current)]])

(defmethod reagent-page :default [& args]
  [not-found-page])

(defn css [css-links]
  (println "css links:" css-links)
  [:div
   (doall (map-indexed
           (fn [i l] ^{:key i} [link-css l])
           css-links))
       ;[link-css "tailwindcss/dist/tailwind.css"]
       ;[link-css "@fortawesome/fontawesome-free/css/all.min.css"]
   ])

(defn status-page [status]
  (let [background-image (get-in-config-cljs [:webly :loading-image-url])
        ;background-image "/r/webly/loading-lemur.jpg"
        ]
    (fn [status]
      [:div
       {:style {:background-image (str "url(" background-image ")") ; no-repeat center center fixed"
                :background-repeat "no-repeat"
                :background-size "cover"
                :justify-content "center"
                :align-items "center"
                :width "100vw"
                :height "100vh"}}
       [:img {:src "/r/webly/loading.svg"
              :style {:width "120px"
                      :height "120px"
                      :position "absolute"
                      :left "50%"
                      :top "50%"
                      :margin "-60px 0 0 -60px"}}]

       [:h1.bg-red-500.m-5
        {:style {:position "absolute"
                 :left "50%"
                 :top "50%"}}
        (str "Webly status: " @status)]])))

(defn webly-app []
  (let [status (subscribe [:webly/status])
        config (subscribe [:webly/config])]
    (fn []
      (let [current-page @current
            {:keys [css-links]} (:webly @config)]
        [:div
         [css css-links]
         [modal-container]
         [notifications-container]
         (if (= @status :running)
           (reagent-page current-page)
           [status-page status])]))))