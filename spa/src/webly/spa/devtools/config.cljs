(ns webly.spa.devtools.config
  (:require
   [re-frame.core :as rf]
   [ui.frisk :refer [frisk]]))

(defn config-page [{:keys [route-params query-params handler] :as route}]
  (let [config (rf/subscribe [:webly/config])]
    (fn []
      [:div
       [:h2.text-2xl.text-blue-700.bg-blue-300 "config"]
       [frisk @config]])))

