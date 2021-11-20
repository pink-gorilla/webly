(ns webly.user.status.view
  (:require
   [re-frame.core :refer [subscribe]]
   [modular.config :refer-macros [get-in-config-cljs]]
   [webly.user.status.subscriptions] ; side-effects
   ))

(defn status-page []
  (let [status (subscribe [:webly/status])
        background-image (get-in-config-cljs [:webly :loading-image-url])
        ;background-image "/r/webly/loading-lemur.jpg"
        ]
    (fn []
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