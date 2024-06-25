(ns demo.helper.ui
  (:require
   [re-frame.core :as rf]))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn block2 [name & children]
  (->  [:div.bg-blue-400.inline-block.w-full ; md:w-full
        {:class "lg:p-2"}
        [:p.text-4xl.pb-2 name]]
       (into children)))