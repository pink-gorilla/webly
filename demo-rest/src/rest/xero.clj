(ns rest.xero
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [martian.core :as martian]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.rest.martian.xero :refer [martian-xero martian-xero-tenant]]))

; this comes from the xero identity token:
; :xero_userid "3c7360c0-6195-462d-b913-76ce9c66cbb8"

(def demo-contact
  {:name "Woo / Bruce Banner"
                :FirstName "Bruce"
                :LastName "Banner"
                :IsCustomer true
                :emailAddress "hulk@avengers.com"
                :phones [{:phoneNumber "555-1212"
                          :phoneType "MOBILE"}]
                :Addresses [{:AddressType "STREET" ; send to address
                             :AttentionTo "Bruce Banner II."
                             :AddressLine1 "1808 SW F Ave"
                             :AddressLine2 ""
                             :AddressLine3 ""
                             :AddressLine4 ""                             
                             :City "Lawton"
                             :PostalCode "73501"
                             :Region "OK"
                             :Country "USA"}
                            {:AddressType "POBOX" ; invoice address
                             :AttentionTo "Bruce Banner I."
                             :AddressLine1 "400 Medinah Rd"
                             :AddressLine2 ""
                             :AddressLine3 ""
                             :AddressLine4 ""
                             :City "Roselle"
                             :PostalCode "60172"
                             :Region "IL", 
                             :Country "USA"}]
                :ContactGroups [{:ContactGroupID "f1b7e1fd-c95a-4f64-b8de-9fa10e7dcb57"}]     
                             })


 (def demo-invoice 
   {:LineAmountTypes "Exclusive"
    :AmountDue 33.8, 
                  :LineItems [{:Quantity 17.0
                               :UnitAmount 1.4
                               :LineAmount 23.8
                               :ItemCode "E19"}
                               {:Quantity 1.0
                               :UnitAmount 100.3
                               :LineAmount 100.3
                               :ItemCode "SHP"}
                               ]
                  :DueDateString "2022-02-13T00:00:00"
                  :CurrencyCode "USD"
                  :BrandingThemeID "f449e7d8-aa59-4554-b94c-95973b4a5688"
                  :DateString "2022-01-13T00:00:00"
                  ;:Total 23.8
                  :Date "/Date(1642032000000+0000)/"
                  ;:TotalTax 0.0
                  :Type "ACCREC"
                  :Contact {:ContactID "bb06b2e0-45aa-4300-8e87-1b25b37b4c58"}
                  :DueDate "/Date(1644710400000+0000)/"
                  :Reference "2022-7777"
                  :Status "DRAFT"})


(defn print-invoices [invoices]
  (->> invoices
      (map #(select-keys % [:Type :Status :InvoiceNumber  :DateString :Name :Total
            ;  :DueDateString :AmountDue
             ;:InvoiceID :UpdatedDateUTC 
            ]))
       (print-table)))


(def invoice-query 
  {:where ;"Date >= DateTime(2022, 01, 01)"
              "(Type == \"ACCREC\")"       
              ;"Date >= DateTime(2022, 01, 01) AND (Type == \"ACCREC\")" ;var invoicesFilter = "Date >= DateTime(" + sevenDaysAgo + ")";
              ; Contact.Name=="Basket Case" AND Type=="ACCREC" AND STATUS=="AUTHORISED"
              ; Type=="BANK" Type=="ASSET"
              ; Status=="VOIDED" OR Status=="DELETED"
              ; Name.Contains("Peter")  Name.StartsWith("") Name.EndsWith("")
              ; 'Reference != null AND Reference.EndsWith("' + invoiceReference + '")'
              ; EmailAddress!=null&&EmailAddress.StartsWith("boom")
              ; Date >= DateTime(2015, 01, 01) && Date < DateTime(2015, 12, 31)
              ; ifModifiedSince: Date = new Date("2020-02-06T12:17:43.202-08:00");
    :page 1 ; page=1 – Up to 100 invoices will be returned in a single API call with line items shown for each invoice
    :unitdp 4 ; (Unit Decimal Places) You can opt in to use four decimal places for unit amounts
    :summaryOnly false ;  retrieve a smaller version of the response object. This returns only lightweight fields, excluding computation-heavy fields from the response, making the API calls quick and efficient.
    :includeArchived false ;  Invoices with a status of ARCHIVED will be included in the response
    ;:order  'InvoiceNumber ASC';
    ;:IDs ; Filter by a comma-separated list of InvoicesIDs (uuid)
    ;:InvoiceNumbers ; Filter by a comma-separated list of InvoiceNumbers.
    ;  ["INV-001", "INV-002"];
    ;:ContactIDs ; Filter by a comma-separated list of ContactIDs (uuid)
    ;:Statuses ; Filter by a comma-separated list Statuses. For faster response times we recommend using these explicit parameters instead of passing OR conditions into the Where filter.
    ;  ["DRAFT", "SUBMITTED"];
    ;:createdByMyApp When set to true you'll only retrieve Invoices created by your app
    })



(defn xero []
  (refresh-auth-token :xero)
  (let [m (martian-xero)
        tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"
        t (martian-xero-tenant tenant-id)
        invoice-id "e10557d0-f6d4-4a86-b790-6a93e8281a52"
        ;t (header-xero-tenant tenant-id)
        ]

    (->> (martian/request-for m :userinfo {})
          (info "xero userinfo request: "))

    (->> (martian/response-for m :tenants {})
          :body
          pr-str
          (info "xero tenants: "))

    (->> 
       (martian/response-for t :tenants {}) 
       :body
       (into [])
       (map #(select-keys % [:tenantId :tenantType :tenantName]))
       (print-table)
       ;(info "tenants: ")
       )


    (->> (martian/response-for t :userinfo {})
          :body
          (info "xero userinfo: "))

    (->> 
      (martian/response-for t :branding-themes {}) 
      :body
      :BrandingThemes
      (map #(select-keys % [:BrandingThemeID :Name :Type]))
      (print-table)
       ;(info "xero branding themes: ")
      )

    (->> (martian/response-for t :invoice {:invoice-id invoice-id})
         :body
         (info "invoice: "))  

   
    (let [contact-id "4cff70ca-5042-423e-a418-e41b3cf0ca9c"]
       (->> (martian/response-for t :contact {:contact-id contact-id})
          :body 
          (info "contact: "))) 


     (->> (martian/response-for t :contact-group {}) 
           :body
           :ContactGroups
           (map #(select-keys % [:ContactGroupID :Name :Status]))
           (print-table))

      (let [group-id "f1b7e1fd-c95a-4f64-b8de-9fa10e7dcb57"
            contact-id (->> (martian/response-for t :contact-create {:contacts [demo-contact]})
                            :body
                            :Contacts
                            first 
                            :ContactID)
            _ (info "added contact id: " contact-id)
            _ (->> (martian/response-for t :add-contacts-to-group 
                                           {:group-id group-id
                                            :Contacts [{:contactID contact-id}]})
                   :body
                   (info "contact-group-add-contact: "))
           ])

       ; this adds real invoices. not good for production :-)
       #_(->> (martian/response-for t  :invoice-create
                                    {:invoices [demo-invoice ]})
                   :body
                   :Invoices
                   first
                   :InvoiceID
                   (info "invoice created: "))

       (->> (martian/response-for t :invoice-list invoice-query)
            :body
            :Invoices
            print-invoices)

       (->> (martian/response-for t :invoice-list-since 
                                   {:where "(Type == \"ACCREC\")"       
                                    :page 1})
            :body
            :Invoices
            print-invoices)



     
;(get-request :xero/contacts) );

   

; **
    ))
