(ns rest.schema
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [martian.core :as martian]
   [funes.core :as f]
   [funes.schema :as s]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.rest.paging :refer [request-paginated]]
   [modular.rest.martian.xero :refer [martian-xero martian-xero-tenant]]))


; generate schema from rest response
; https://github.com/txus/funes

(defn infer-schema []
  (refresh-auth-token :xero)
  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"
        t (martian-xero-tenant tenant-id)
        ;invoice-id "e10557d0-f6d4-4a86-b790-6a93e8281a52"
        ;t (header-xero-tenant tenant-id)
        invoice-r (martian/response-for t :invoice-list ; -since 
                                   {;:where "(Type == \"ACCREC\")"       
                                    :page 1})
        invoices (->> invoice-r                  
                      :body
                      :Invoices)
        params {;:where "(Type == \"ACCREC\")"
                } ;"Date >= DateTime(2022, 01, 01)"

        invoices (request-paginated t :invoice-list params :Invoices)             
        contacts (->> (martian/response-for t :contact-list
                                   {:page 1})
                  :body
                  :Contacts) 
        ]

     (info "invoices used to infer: " (count invoices))
     (->> (f/->schema invoices)
          (info "inferred schema for invoices: "))

      (->> invoices
        (reduce f/infer)
        ;s/generalize-values ;; you can skip this part if you want maximum concreteness
        s/generate-schema
        (info "inferred schema (maximum concreteness): ")
        )
    
     (info "contacts used to infer: " (count contacts))
     (->> (f/->schema contacts)
          (info "inferred schema for contacts: "))

     
     ;(info "contacts: " (pr-str contacts))
     ;(info "invoices: " (pr-str invoices))
     (spit "/tmp/invoices.edn" (pr-str invoices))
     (spit "/tmp/contacts.edn" (pr-str contacts))

;
  ))