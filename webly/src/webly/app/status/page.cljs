(ns webly.app.status.page
  (:require
   [re-frame.core :refer [subscribe]]
   [modular.config :refer-macros [get-in-config-cljs]]
   [webly.app.status.subscriptions] ; side-effects
   [webly.app.status.events] ; side-effects
   ))

(defn status-page []
  (let [status (subscribe [:webly/status])
        loading-image-url (get-in-config-cljs [:webly :loading-image-url])
        spinner (get-in-config-cljs [:webly :spinner])
        prefix (get-in-config-cljs [:prefix])

        ;background-image "/r/webly/loading-lemur.jpg"
        ]
    (fn []
      [:div
       {:style {:background-image (str "url(" prefix loading-image-url ")") ; no-repeat center center fixed"
                :background-repeat "no-repeat"
                :background-size "cover"
                :justify-content "center"
                :align-items "center"
                :width "100vw"
                :height "100vh"}}
       [:img {:src (str prefix spinner)  ; "/r/" "webly/loading.svg"
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