(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]

   [clj-jwt.core :as clj-jwt]
   [buddy.sign.jwt :as jwt]
   [buddy.sign.jws :as jws]
   [buddy.auth :as auth]

   [modular.base64 :refer [base64-decode]]
   [modular.config :as config]
   [modular.oauth2.store :refer [load-token]]
   [modular.oauth2.request :refer [get-endpoint get-request get-request-xero]])
  (:import
   [com.auth0.jwt JWT]))



(defn github []
  ; github
  (load-token :github)
  (get-endpoint :github/userinfo)

  (->> (get-request :github/userinfo)
       (info "github/userinfo: "))

  (info "github/search-repo ")
  (->> (get-request :github/search-repo {:q "user:pink-gorilla"})
       :items
       ;first
       (map #(select-keys % [:name :forks_count :watchers_count :open_issues_count]))
       (print-table)))

(defn google []
  (load-token :google)
  (get-endpoint :google/search)
  (get-request :google/userinfo)
  (get-request :google/drive-files-list)
  (get-request :google/search {:q "clojure"
                               :num 10
                             ;:cx 4
                               }))

(defn xero []
  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"]
    (load-token :xero)
    ;(get-endpoint :xero/contacts)
    ;(get-request :xero/contacts) ; xero.accountingApi.getBrandingThemes (tenantId);

    (->> (get-request-xero tenant-id :xero/branding-themes {}) ; xero.accountingApi.getBrandingThemes (tenantId);
         (info "xero branding themes: "))

; Authorization Bearer ey****
    ; Xero-Tenant-Id 791f3cb4-97b9-45f9-b5e6-7319cda87626
    ))

(defn make-web-requests [{:keys [provider]}]
  (config/set!
   :oauth2
   {:token-path "../demo-webly/.webly/tokenstore/"})
  (case provider
    :xero (xero)
    :google (google)
    :github (github)))

;; TOKEN


(defn jwt [& _]
  (let [token (jwt/sign {:userid 1} "secret")
        data (jwt/unsign token "secret")]
    (info "jwt data: " data " token: " token)))


;       (base64-decode "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")

;   (auth/authenticated? {:identity "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"})

(defn show-token [& _]
  (->> "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"
       ;(jws/decode-header )
       clj-jwt/str->jwt
      ; :claims
      ;(println "token: ")
       ))

(defn get-token [p]
  (->  (load-token p)
       :access-token
       clj-jwt/str->jwt
       ; JWT/decode 
       ; (.getIssuer)
  )    )


(defn show-header [p]
  (-> p get-token :header))

(defn show-claims [p]
  (-> p get-token :claims))

(defn show-signature [p]
  (-> p get-token :signature))

(defn show-encoded-data [p]
  (-> p get-token :encoded-data))


 ;:header
        
(show-header :xero)
(show-claims :xero)
(show-signature :xero)
(show-encoded-data :xero)

{:aud "https://identity.xero.com/resources", 
 :sub "d86a52218b89501b814fb2065b5973e1", 
 :iss "https://identity.xero.com", 
 :exp 1637518336, 
 :scope ["email" "profile" "openid" "accounting.reports.read" "accounting.settings" "accounting.attachments" "accounting.transactions" "accounting.journals.read" "accounting.transactions.read" "accounting.contacts" "offline_access"], :xero_userid "3c7360c0-6195-462d-b913-76ce9c66cbb8", :auth_time 1637516530, :jti "2e68c7df3f7e30f84ac45c2c30aadbb7", :nbf 1637516536, :global_session_id "c735d534d0204f4da4f8100c1437fe3e", 
 :authentication_event_id "894f5c8c-3938-416d-966e-8f28fa24d358", 
 :client_id "6E4D953BAAE949D1A1B399307AD58B94"}







