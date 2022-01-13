(ns rest.xero
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.token.store :refer [load-token]]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.oauth2.request :refer [get-request post-request put-request get-request-xero]]))


(defn xero []
  (refresh-auth-token :xero)

  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"]
    (load-token :xero)
    ;(get-endpoint :xero/contacts)
    ;(get-request :xero/contacts) ; xero.accountingApi.getBrandingThemes (tenantId);

    (->> ; xero.accountingApi.getBrandingThemes (tenantId);
         (get-request-xero tenant-id :xero/branding-themes {}) 
         :BrandingThemes
         (map #(select-keys % [:BrandingThemeID :Name :Type]))
         (print-table)
         ;(info "xero branding themes: ")
         )

; Authorization Bearer ey****
    ; Xero-Tenant-Id 791f3cb4-97b9-45f9-b5e6-7319cda87626
    ))
