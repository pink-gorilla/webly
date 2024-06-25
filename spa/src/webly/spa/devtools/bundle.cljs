(ns webly.spa.devtools.bundle)

(defn bundle-page [{:keys [_route-params _query-params _handler] :as _route}]
  [:iframe {:src "/r/bundlesizereport.html"
            :title "bundle size"
            :height "100%"
            :width "100%"}]
  #_[:div
     [:a {:href "/r/bundlesizereport.html"}
      [:p "show bundlesize stats"]]])
