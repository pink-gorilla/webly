(ns webly.user.oauth2.page
  (:require
   [reagent.core :as r]
   [webly.web.handler :refer [reagent-page]]
   [webly.user.oauth2.redirect :refer [sendcallback]]))

(defn oauth-redirect [provider]
  (let [state (r/atom :LOGGING_IN)]
    (r/create-class
     {:component-did-mount (fn [this]
                             (println "redirect did mount..")
                             (let [;a @current
                                   ;r (.handleRedirect a)
                                   r :SUCCESS]
                               (sendcallback provider)
                               (if (= r "SUCCESS")
                                 (reset! state :SUCCESS)
                                 (reset! state :FAILED))))
      :reagent-render (fn []
                        [:p (case @state
                              :LOGGING_IN "Logging you in…"
                              :SUCCESS "You may close this window."
                              :FAILED "Login failed. Please close this window and try again.")])})))

(defmethod reagent-page :oauth2/redirect [{:keys [route-params query-params handler] :as args}]
  (let [{:keys [provider]} route-params
        provider-kw (keyword provider)]
    (println "redirect: " args)
    [oauth-redirect provider-kw]))

