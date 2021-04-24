(ns webly.user.dialog
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require
   [re-frame.core :refer [reg-sub-raw reg-event-db dispatch subscribe]]
   [webly.user.config.core :refer [link-css]]))

; stolen from:
; https://github.com/benhowell/re-frame-modal


; todo: incorporate this
; https://www.creative-tim.com/learning-lab/tailwind-starter-kit/documentation/vue/modals/small

(reg-event-db
 :modal/open
 (fn [db [_ child size close]]
   (assoc-in db [:modal]
             {:show? true
              :child child
              :close (or close nil) ; optionally dispatch on close reframe event
              :size (or size :default)})))

(reg-event-db
 :modal/close
 (fn [db [_]]
   (let [{:keys [show? close]} (:modal db)]
     (if show?
       (do (when close
             (dispatch close))
           (assoc-in db [:modal] {:show? false
                                  :child nil
                                  :size :default
                                  :close nil}))
       db))))

(defn modal-panel
  [{:keys [child size]}]
  [:div {:class "modal-wrapper"}
   [:div {:class "modal-backdrop"
          :on-click (fn [event]
                      (dispatch [:modal/close])
                      (.preventDefault event)
                      (.stopPropagation event))}]
   [:div {:class "modal-child"
          :style {:width (case size
                           :extra-small "15%"
                           :small "30%"
                           :large "70%"
                           :extra-large "85%"
                           "50%")}} child]])

(reg-sub-raw
 :modal
 (fn [db _] (reaction (:modal @db))))

(defn modal-container []
  (let [modal (subscribe [:modal])]
    (fn []
      [:div
       (when (:show? @modal)
         [modal-panel @modal])])))







