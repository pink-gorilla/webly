(ns demo
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.config :as config]
   [modular.oauth2.store :refer [load-token]]
   [modular.oauth2.request :refer [get-endpoint get-request]]))

(defn make-web-requests [& _]

  (config/set!
   :oauth2
   {:token-path "../../.webly/tokenstore/"})

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
       (print-table))

  (load-token :google)
  (get-endpoint :google/search)
  (get-request :google/userinfo)
  #_(get-request :google/search {:q "clojure"
                                 :num 10
                             ;:cx 4
                                 })

  (load-token :xero)

;  
  )

