(ns modular.oauth2.provider.xero
  (:require
   [modular.oauth2.protocol :refer [provider-config]]))

(defn parse-authorize-response [{:keys [query]}] ;anchor
  {:scope (:scope query)
   :code (:code query)})

;; api requests

(defn api-request-auth-header [token]
  {"Authorization" (str "Bearer " token)})

(defn user-parse [data]
  {:user (get-in data [:Organisations :Name])
   :email "no email"})

(def config
  {; authorize
   :authorize-uri "https://login.xero.com/identity/connect/authorize"
   :authorize-response-type "code"
   :parse-authorize-response parse-authorize-response
   ; token  
   :token-uri "https://identity.xero.com/connect/token"
   :accessTokenResponseKey "id_token"
   ; api requests
   :auth-header api-request-auth-header
   :endpoints {; https://api-explorer.xero.com/
               :userinfo "https://api.xero.com/api.xro/2.0/Organisation"
               :tenants "https://api.xero.com/connections"
               :contacts "https://api.xero.com/api.xro/2.0/Contacts"
               :invoice "https://api.xero.com/api.xro/2.0/Invoices/"
               :branding-themes "https://api.xero.com/api.xro/2.0/BrandingThemes"}
   ; userinfo
   :user "https://api.xero.com/api.xro/2.0/Organisation"
   :user-parse user-parse})

(defmethod provider-config :xero [_]
  config)

;; Xero example for authroize request

; https://login.xero.com/identity/connect/authorize
; ?response_type=code
; &client_id=YOURCLIENTID
; &redirect_uri=YOURREDIRECTURI
; &scope=openid profile email accounting.transactions
; &state=123

; https://xeroapi.github.io/xero-node/accounting/index.html
; https://github.com/XeroAPI/xero-node/blob/master/src/gen/api/accountingApi.ts


; var paginate = require("../paginate/paginate.js").paginate;

  ;var response = await xero.accountingApi.getContact(tenantId, contactID);

   ; https://xeroapi.github.io/xero-node/accounting/index.html#api-Accounting-getInvoice

   ; /Invoices/{InvoiceID}


;; postman url:
;; https://app.getpostman.com/run-collection/d069793e904f7602770d#?env%5BOAuth%202.0%5D=W3sia2V5IjoiY2xpZW50X2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6ImNsaWVudF9zZWNyZXQiLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoicmVmcmVzaF90b2tlbiIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJhY2Nlc3NfdG9rZW4iLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoieGVyby10ZW5hbnQtaWQiLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoicmVfZGlyZWN0VVJJIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6InNjb3BlcyIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJzdGF0ZSIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX1d



(defn header-xero-tenant [tenant-id]
  {"Xero-Tenant-Id" tenant-id})
   