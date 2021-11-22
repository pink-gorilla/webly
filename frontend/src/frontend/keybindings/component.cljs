(ns frontend.keybindings.component
  (:require
   [taoensso.timbre :refer-macros [debug]]
   [re-frame.core :refer [subscribe dispatch dispatch-sync]]))

;; search-box

(defn search-box [query]
  [:div.flex.flex-row.items-stretch.mb-0.border.border-solid.border-blue-800.p-0
   [:input {:class "w-full ml-2 bg-blue-200 text-lg"
            :type        "text"
            :value       query
            :on-change   #(dispatch [:palette/filter-changed (-> % .-target .-value)])
            :on-key-down #(dispatch-sync [:palette/filter-keydown (.-which %)])
              ;; TODO  : on-blur kicks in before menu gets the click, but we want
              ;; :on-blur   #(dispatch [:palette-blur])
              ;; :on-mouse-down #(dispatch [:palette-blur])
            :ref  #(when % (.focus %)) ; make input focused
            }]
   [:div
    [:i.fas.fa-times-circle.pr-2
     {:on-click #(dispatch [:modal/close])}]]])

;; keybinding-list

(defn scroll-into-view [el]
  (when el
    (debug "scroll-into-view")
    (if (.-scrollIntoViewIfNeeded el)
      (.scrollIntoViewIfNeeded el false)
      (.scrollIntoView el false))))

(defn palette-item [item active?]
  [:div
   {:class (when active? "bg-red-300")
    :ref #(when active? (scroll-into-view %))}
   [:li.flex.flex-row.items-stretch
    {:on-click #(dispatch-sync [:palette/action item])}
    [:span.w-full (:desc item)]
    [:span.m-2.border.border-round.border-blue-400
     {:class "w-1/4"}
     (:kb item)]]])

(defn keybinding-list [visible-items highlight]
  [:div {:class "mt-0 pt-0"
         :style {:overflow-y "auto"
                 :max-height "300px"
                 :background "#f5f5f5"}}
   (when visible-items
     (into [:ul]
           (map-indexed (fn [i x]
                          [palette-item x (= i highlight)])
                        visible-items)))])

;; dialog

(defn keybindings-dialog
  []
  (let [palette (subscribe [:palette])
        {:keys [query visible-items highlight]} @palette]
    [:div.bg-blue-200
     [search-box query]
     [keybinding-list visible-items highlight]]))