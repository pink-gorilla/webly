(ns webly.user.analytics.view
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [subscribe dispatch]]
   [webly.user.analytics.subscriptions] ; side-effects
   [webly.user.analytics.events] ; side-effects
   [webly.user.analytics.google-tag :refer [script-tag-src script-tag-config]]))

(defn google-analytics-container []
  (let [config (subscribe [:analytics/config])]
    (fn []
      (let [{:keys [enabled id]} @config]
        (when (and enabled id)
          (info "google analytics starting with google id: " id)
          (dispatch [:ga/init])
          [:div
            ; [script-analytics id]  
           ;[script-tag-src id]
           ;[script-tag-config id]
           ])))))





