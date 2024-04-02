(ns webly.app.status.page
  (:require
   [taoensso.timbre :refer-macros [info infof error]]
   [re-frame.core :refer [subscribe]]
   [webly.app.status.subscriptions] ; side-effects
   [webly.app.status.events] ; side-effects
   [webly.app.mode :refer [get-resource-path]]))

(defn status-page []
  (let [status (subscribe [:webly/status])
        config (subscribe [:webly/config])
        loading-image-url (get-in @config [:spa :loading-image-url])
        spinner (get-in @config [:spa :spinner])
        prefix (get-resource-path)]
    (info "compile time config " {:webly {:spinner spinner
                                          :loading-image-url loading-image-url}})
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