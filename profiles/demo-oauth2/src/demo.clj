(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.config :as config]
   [modular.oauth2.store :refer [load-token]]
   [modular.oauth2.request :refer [get-endpoint get-request get-request-xero]]))

(defn google []
  (load-token :google)
  (get-endpoint :google/search)
  (get-request :google/userinfo)
  #_(get-request :google/search {:q "clojure"
                                 :num 10
                             ;:cx 4
                                 }))

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
   {:token-path "../../.webly/tokenstore/"})
  (case provider
    :xero (xero)
    :google (google)
    :github (github)))

