(ns webly.user.oauth2.page
  (:require
   [cemerick.url :as url]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [webly.web.handler :refer [reagent-page]]))

(def bc (js/BroadcastChannel. "oauth2_redirect_channel"))

(defn window-data []
  (let [url (-> (.. js/window -location -href)
                (url/url))]
    {:anchor (url/query->map (:anchor url))
     :query (:query url)}))

(defn oauth-redirect [provider]
  (let [state (r/atom :LOGGING_IN)]
    (r/create-class
     {:component-did-mount (fn [this]
                             (println "redirect did mount..")
                             (let [wd (window-data)
                                   ;a @current
                                   ;r (.handleRedirect a)
                                   r :SUCCESS]
                               (println "window data:" wd)
                               (.postMessage bc (pr-str (merge {:provider provider} wd)))
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

