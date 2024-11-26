(ns webly.spa.loader.page
  (:require
   [taoensso.timbre :refer-macros [info infof error]]
   [re-frame.core :refer [subscribe]]
   [webly.spa.loader.subscriptions] ; side-effects
   [webly.spa.loader.events] ; side-effects
   [webly.spa.mode :refer [get-resource-path]]))

(defn loader-page []
  (let [status (subscribe [:webly/status])
        config (subscribe [:webly/config])]
    (fn []
      (let [loading-image-url (get-in @config [:spa :loading-image-url])
            spinner (get-in @config [:spa :spinner])
            prefix (get-resource-path)]
        (info "loader config: " {:webly {:spinner spinner
                                         :loading-image-url loading-image-url}})
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
          (str "Webly status: " @status)]]))))