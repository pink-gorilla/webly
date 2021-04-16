(ns webly.user.oauth2.handler-redirect
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [hiccup.page :as page]
   [ring.util.response :as response]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-webly]]))

(defn page-oauth-redirect [provider]
  (page/html5
   {:mode :html}
   [:body
    [:div
     [:script {:src "/r/webly/redirect.js"
               :type "text/javascript"
               :onload (str "sendcallback ('" provider "');")}]
     "Logging you in..."
     [:div "Feel free to close this window if it doesn't go away automatically."]]]))

(defn handler-oauth-redirect [req]
  (info "redirect: " req)
  (let [provider (get-in req [:route-params :provider])
        res (response/content-type {:status 200
                                    :body (page-oauth-redirect provider)}
                                   "text/html")]
    (info "provider: " provider)
    res))

(def handler-oauth-redirect-wrapped
  (-> handler-oauth-redirect
      wrap-webly))

(add-ring-handler :oauth2/redirect handler-oauth-redirect-wrapped)


