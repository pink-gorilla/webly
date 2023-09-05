(ns demo.pages.main.oauth
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [modular.oauth2.user.view :refer [user-login]]
   [modular.oauth2.token.ui :refer [provider-status-grid]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; OAUTH
(defn demo-oauth []
  [block2 "oauth2"
   [user-login]
   [:div.border.border-blue-500.border-2.border-round ; .overflow-scroll
    [provider-status-grid [:google :github :xero :woo :wordpress
                           :wunderbar]]] ; not available. does not have token.
   ])