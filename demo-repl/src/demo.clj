(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [init]

   [clj-jwt.core :as clj-jwt]
   [buddy.sign.jwt :as jwt]
   [buddy.sign.jws :as jws]
   [buddy.auth :as auth]

   [modular.base64 :refer [base64-decode]]
   [modular.config :as config]
   [modular.oauth2.store :refer [load-token]]
   [modular.oauth2.refresh :refer [refresh-auth-token]]
   [modular.oauth2.request :refer [get-endpoint get-request get-request-xero post-request]]
   [gsheet]
   )
  (:import
   [com.auth0.jwt JWT]))


(defn github []
  ; github token does not expire.
  ;(load-token :github)
  ;(get-endpoint :github/userinfo)

  (->> (get-request :github/userinfo)
       (info "github/userinfo: "))

  (info "github/search-repo ")
  (->> (get-request :github/search-repo {:q "user:pink-gorilla"})
       :items
       ;first
       (map #(select-keys % [:name :forks_count :watchers_count :open_issues_count]))
       (print-table)))




(defn google []
  (refresh-auth-token :google)
  (info "google/userinfo: " (get-request :google/userinfo))
  #_(->> (get-request :google/drive-files-list)
       :files
       (map #(dissoc % :kind :id :mimeType))
       (print-table))

 ; (get-request :google/search {:q "clojure" :num 10})
  (gsheet/modify-cells)
  

  ;
  )

(defn xero []
  (refresh-auth-token :xero)

  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"]
    (load-token :xero)
    ;(get-endpoint :xero/contacts)
    ;(get-request :xero/contacts) ; xero.accountingApi.getBrandingThemes (tenantId);

    (->> (get-request-xero tenant-id :xero/branding-themes {}) ; xero.accountingApi.getBrandingThemes (tenantId);
         (info "xero branding themes: "))

; Authorization Bearer ey****
    ; Xero-Tenant-Id 791f3cb4-97b9-45f9-b5e6-7319cda87626
    ))


;; CLI ENTRY POINT ************************************

(defn make-web-requests [{:keys [provider]}]
  (case provider
    :xero (xero)
    :google (google)
    :github (github)
    ;
    ))














