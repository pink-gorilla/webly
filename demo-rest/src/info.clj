(ns info
  (:require
     [taoensso.timbre :as timbre :refer [info error]]
     [init]
     [modular.oauth2.token.store :refer [load-token]]
     [modular.oauth2.token.info :refer [get-header get-claims get-signature get-encoded-data]]
  ))
  

(defn show-keys [p]
  (info "keys: " p 
     (-> (load-token p)
         keys)))


(defn show-header [p t]
  (info "header: " p t (get-header p t)))

(defn show-claims [p t]
  (info "claims: " p t (get-claims p t)))

(defn show-signature [p t]
  (info "signature: " p t  (get-signature p t)))

(defn show-encoded-data [p t]
  (info "data: " p t (get-encoded-data p t)))  

(defn show [& _]

  ;; xero ************************************************************

  ;(show-header :xero :access-token)
  #_{:alg "RS256"
     :kid "1CAF8E66772D6DC028D6726FD0261581570EFC19"
     :typ "JWT"
     :x5t "HK-OZnctbcAo1nJv0CYVgVcO_Bk"}

  (show-claims :xero :access-token)
  #_{:aud "https://identity.xero.com/resources"
     :sub "d86a52218b89501b814fb2065b5973e1"
     :iss "https://identity.xero.com"
     :exp 1637518336
     :scope ["email" "profile" "openid" "accounting.reports.read" "accounting.settings" "accounting.attachments" "accounting.transactions" "accounting.journals.read" "accounting.transactions.read" "accounting.contacts" "offline_access"]
     :xero_userid "3c7360c0-6195-462d-b913-76ce9c66cbb8"
     :auth_time 1637516530
     :jti "2e68c7df3f7e30f84ac45c2c30aadbb7"
     :nbf 1637516536
     :global_session_id "c735d534d0204f4da4f8100c1437fe3e"
     :authentication_event_id "894f5c8c-3938-416d-966e-8f28fa24d358"
     :client_id "6E4D953BAAE949D1A1B399307AD58B94"}
  
  ;; tenant id is not matching ...
  ;; var tenantId = "791f3cb4-97b9-45f9-b5e6-7319cda87626";
  ;(show-signature :xero :access-token)
  ;(show-encoded-data :xero :access-token)
 

  ;(show-header :xero :id_token)
  (show-claims :xero :id-token)

  ;; google ************************************************************

  ;(show-keys :google)
  ;(show-header :google :id-token)
  (show-claims :google :id-token)


;(show-claims :google :refresh_token)
;  (show-claims :google :access-token)


(show-claims :github :access-token)

  ; this only works for xero.
  ;(get-token :google :access-token)
  ;(get-token :google :refresh_token)
  ; (clj-jwt/str->jwt "4/0AX4XfWggw7nvl0I00ntBey321QJxwPbm0w1q8dw4ijBORnOO74ccGWvMMBui6Og_6VtwOw")




  (info "done.")
)


