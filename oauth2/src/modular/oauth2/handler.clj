(ns modular.oauth2.handler
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.authorize.redirect-handler :refer [handler-oauth2-redirect]]
   [modular.oauth2.authorize.token-handler :refer [token-handler]]
   [modular.oauth2.store.save-handler :refer [handler-oauth2-save]]
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.webserver.handler.registry :refer [add-ring-handler]]
   [modular.ws.middleware :refer [wrap-ws]]))

(def handler-oauth-redirect-wrapped
  (-> handler-oauth2-redirect
      wrap-ws))

(def token-handler-wrapped
  (-> token-handler
      wrap-api-handler))

(def handler-oauth2-save-wrapped
  (-> handler-oauth2-save
      wrap-api-handler))

(add-ring-handler :oauth2/redirect handler-oauth-redirect-wrapped)
(add-ring-handler :oauth2/token token-handler-wrapped)
(add-ring-handler :oauth2/save-token handler-oauth2-save-wrapped)