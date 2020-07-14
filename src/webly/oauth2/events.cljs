(ns webly.oauth2.events
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [re-frame.core :refer [reg-event-fx dispatch]]))

(defn clear-error [state]
  (assoc-in state [:user-auth :error] nil))

(defn message-event-handler
  "Message handler for oauth login (from ::open-oauth-window).
  This is a named function to prevent the handler from being added
  multiple times."
  [e]
  (info "message received: " e)
  (dispatch [::remote-oauth (.. e -data -code) (.. e -data -state)]))

(reg-event-fx
 :oauth2/open-window
 (fn [{db :db} [_ provider]]
   (js/window.addEventListener "message" message-event-handler)
   (case provider
     :github (do (info "github oauth2 ..")
                 (.open js/window
                        "/oauth2/github/auth"
                        "GitHub OAuth"
                        "width=500,height=600")
                 (info "widow shoud have been opened.."))
     (error "unknown oauth2 provider: " provider))
   {:db (-> db
            clear-error
            (assoc-in [:user-auth :oauth-provider] provider))}))