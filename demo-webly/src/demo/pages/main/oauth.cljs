(ns demo.pages.main.oauth
  (:require
   [taoensso.timbre :refer-macros [debug info warn error]]
   [re-frame.core :as rf]
   [modular.oauth2.view :refer [tokens-view user-button]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]))

;; OAUTH
(defn demo-oauth []
  [block2 "oauth2"
   [user-button :github]
   [user-button :google]
   [user-button :xero]
   [:p [link-dispatch [:oauth2/login :github] "github login via popup"]]
   [:p [link-dispatch [:oauth2/login :google] "google login via popup"]]
   ; [:p [:a.bg-blue-300 {:href "/oauth2/github/auth"} "github login via page-redirect (needs creds.edn)"]]
   [:div.border.border-blue-500.border-2.border-round.overflow-scroll
    [tokens-view]]])