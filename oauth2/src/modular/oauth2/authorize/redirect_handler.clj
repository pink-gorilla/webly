(ns modular.oauth2.authorize.redirect-handler
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [hiccup.page :as page]
   [ring.util.response :as response]))

(defn page-oauth2-redirect [provider]
  (page/html5
   {:mode :html}
   [:body
    [:div
     [:script {:src "/r/webly/redirect.js"
               :type "text/javascript"
               :onload (str "sendcallback ('" provider "');")}]
     "Logging you in..."
     [:div "Feel free to close this window if it doesn't go away automatically."]]]))

(defn handler-oauth2-redirect [req]
  (warn "oauth2/authorize-response: " req)
  (let [p (:params req)
        qp (:query-params req)]
    (info "oauth2/authorize-response: params:" p)
    (info "oauth2/authorize-response: query-params:" qp)
    (let [provider (get-in req [:route-params :provider])
          res (response/content-type {:status 200
                                      :body (page-oauth2-redirect provider)}
                                     "text/html")]
      (info "oauth2 redirect for provider: " provider)
      res)))
