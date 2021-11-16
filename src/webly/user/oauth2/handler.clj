(ns webly.user.oauth2.handler
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [hiccup.page :as page]
   [ring.util.response :as response]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-webly wrap-api-handler]]
   [modular.oauth2.authorize.handler-redirect :refer [handler-oauth2-redirect]]
   [modular.oauth2.authorize.handler-redirect-github :refer [handler-github-redirect]]
   [modular.oauth2.store.save-handler :refer [handler-oauth2-save]]))

(def handler-oauth-redirect-wrapped
  (-> handler-oauth2-redirect
      wrap-webly))

(add-ring-handler :oauth2/redirect handler-oauth-redirect-wrapped)

(def handler-github-redirect-wrapped
  (-> handler-github-redirect
      wrap-api-handler))

(add-ring-handler :oauth2/redirect-github handler-github-redirect-wrapped)

(def handler-oauth2-save-wrapped
  (-> handler-oauth2-save
      wrap-api-handler))

(add-ring-handler :oauth2/save-token handler-oauth2-save-wrapped)