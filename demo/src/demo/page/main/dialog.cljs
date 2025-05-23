(ns demo.page.main.dialog
  (:require
   [re-frame.core :as rf]
   [frontend.notification :refer [show-notification]]
   [demo.helper.ui :refer [link-fn block2]]))

;; DIALOG
(def compile-error
  [:span.bg-green-600.m-3
   [:p.text-red-900 "compile error:" [:b "symbol banana not known"]]
   [:p "line: 12 col: 28"]])

(defn show-dialog-demo []
  (rf/dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
                :small]))

(defn show-dialog-custom-size []
  (rf/dispatch [:modal/open
                [:div {:style {:width "3cm"
                               :height "10cm"
                               :padding "5px"
                               :border-radius "8px"
                               :box-shadow "0 4px 6px rgba(0, 0, 0, 0.1)"}
                       :class "bg-red-100"}
                 "custom size dialog"]]))

(defn demo-dialog []
  [block2 "dialog"
   [:ol
    [:li [link-fn #(show-notification "welcome to wonderland") "show notification"]]
    [:li [link-fn #(show-notification :error "something bad happened") "show notification - error"]]
    [:li [link-fn #(show-notification :error compile-error 0) "show compile error"]]
    ; dialog
    [:li [link-fn show-dialog-demo "show dialog (small)"]]
    [:li [link-fn show-dialog-custom-size "show dialog (custom)"]]]])
