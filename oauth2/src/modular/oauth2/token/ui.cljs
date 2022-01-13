(ns modular.oauth2.token.ui
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

(rf/reg-sub
 :token/status
 (fn [db [_]]
   (get-in db [:token])))


(defn connect [provider]
  (rf/dispatch [:oauth2/authorize-start provider :oauth2/save-server]))

(defn provider-status [provider status]
  [:<>
    [:div (name provider)]
    [:div status]
    [:div {:on-click #(connect provider)} "connect"]
    ])

(defn provider-status-grid [providers]
  (let [tokens-status (rf/subscribe [:token/status providers])
        provider-status (fn [provider]
                          (provider-status provider (provider tokens-status)))]
     (fn [providers]
       (into 
         [:div.grid.grid-cols-3
          [:div "provider"]
          [:div "status"]
          [:div "c/d"]
          ]
         (map provider-status providers)))))

