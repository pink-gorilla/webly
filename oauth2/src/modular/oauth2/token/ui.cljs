(ns modular.oauth2.token.ui
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

(rf/reg-sub
 :tokens/summary
 (fn [db [_]]
   (get-in db [:tokens/summary])))


(rf/reg-event-db
 :tokens/summary
 (fn [db [_ ts]]
   (info "tokens/summary" ts)
   (assoc-in db [:tokens/summary] ts)
   ))

(defn connect [provider]
  (rf/dispatch [:oauth2/authorize-start provider :oauth2/save-server]))

(defn provider-status [provider status]
  ;(info "provider: " provider " status: " status)
  [:<>
    [:div (name provider)]
    [:div (str (:available status))]
    [:div {:on-click #(connect provider)
           :class "hover:text-blue-700"}
          "connect"]
    ])

(defn provider-status-grid [providers]
  (let [c (rf/subscribe [:ws/connected?])
        tokens-status (rf/subscribe [:tokens/summary providers])
        provider-status (fn [provider]
                          (provider-status provider (provider @tokens-status)))
        todo? (atom true)]
     (fn [providers]
      (info "connected: " @c)
      (when @c
         (when @todo?
             (do (reset! todo? false)
                 (rf/dispatch [:ws/send [:tokens/summary {:providers providers}]]))))

       (into 
         [:div.grid.grid-cols-3
          [:div "provider"]
          [:div "status"]
          [:div "c/d"]
          ]
         (map provider-status providers)))))

