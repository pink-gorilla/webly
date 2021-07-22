(ns demo.pages.main.dialog
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [webly.user.notifications.core :refer [add-notification]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; DIALOG
(def compile-error
  [:span.bg-green-600.m-3
   [:p.text-red-900 "compile error:" [:b "symbol banana not known"]]
   [:p "line: 12 col: 28"]])

(defn show-dialog-demo []
  (rf/dispatch [:modal/open [:h1.bg-blue-300.p-5 "dummy dialog"]
                :small]))

(defn demo-dialog []
  [block2 "dialog"
   [:ol
    [:li [link-fn #(add-notification "welcome to wonderland") "show notification"]]
    [:li [link-fn #(add-notification :error "something bad happened") "show notification - error"]]
    [:li [link-fn #(add-notification :error compile-error 0) "show compile error"]]
    [:li [link-fn show-dialog-demo "show dialog"]]]])
