(ns demo.pages.main.oauth
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [modular.oauth2.user.view :refer [user-button]]
   [modular.oauth2.token.ui :refer [provider-status-grid]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; OAUTH
(defn demo-oauth []
  [block2 "oauth2"
    [:div.border.border-blue-500.border-2.border-round ; .overflow-scroll
     [provider-status-grid [:google :github :xero]]]
  
   ;[user-button :github]
   ;[user-button :google]
   ;[user-button :xero]
 
    
    
    ])