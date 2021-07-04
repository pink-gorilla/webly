(ns demo.helper.ui
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
  ))

(defn link-fn [fun text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:on-click fun} text])

(defn link-dispatch [rf-evt text]
  (link-fn #(rf/dispatch rf-evt) text))

(defn link-href [href text]
  [:a.bg-blue-300.cursor-pointer.hover:bg-red-700.m-1
   {:href href} text])

(defn block2 [name & children]
  (->  [:div.bg-blue-400.m-5.inline-block
             [:p.text-4xl.mb-5 name]
             ]
   (into children)))