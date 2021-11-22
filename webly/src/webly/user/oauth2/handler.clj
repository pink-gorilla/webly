(ns webly.user.oauth2.handler
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
  ; [hiccup.page :as page]
  ; [ring.util.response :as response]
   [modular.oauth2.authorize.redirect-handler :refer [handler-oauth2-redirect]]
   [modular.oauth2.authorize.token-handler :refer [token-handler]]
   [modular.oauth2.store.save-handler :refer [handler-oauth2-save]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.ws.middleware :refer [wrap-ws]]

   [webly.web.handler :refer [add-ring-handler]]))

(def handler-oauth-redirect-wrapped
  (-> handler-oauth2-redirect
      wrap-ws))

(add-ring-handler :oauth2/redirect handler-oauth-redirect-wrapped)

(def token-handler-wrapped
  (-> token-handler
      wrap-api-handler))

(add-ring-handler :oauth2/token token-handler-wrapped)

(def handler-oauth2-save-wrapped
  (-> handler-oauth2-save
      wrap-api-handler))

(add-ring-handler :oauth2/save-token handler-oauth2-save-wrapped)