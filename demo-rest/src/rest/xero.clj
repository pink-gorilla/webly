(ns rest.xero
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.token.store :refer [load-token]]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.oauth2.request :refer [get-request post-request put-request]]
   [modular.oauth2.provider.xero :refer [header-xero-tenant]]))


; this comes from the xero identity token:
; :xero_userid "3c7360c0-6195-462d-b913-76ce9c66cbb8"

(defn xero []
  (refresh-auth-token :xero)
  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"
        invoice-id "e10557d0-f6d4-4a86-b790-6a93e8281a52"
        t (header-xero-tenant tenant-id)]
    (load-token :xero)
    ;(get-endpoint :xero/contacts)
    ;(get-request :xero/contacts) ; xero.accountingApi.getBrandingThemes (tenantId);

    (->> ; xero.accountingApi.getBrandingThemes (tenantId);
       (get-request :xero/tenants {} t "") 
       (into [])
       (map #(select-keys % [:tenantId :tenantType :tenantName]))
       (print-table)
       ;(info "tenants: ")
       )

    (->> ; xero.accountingApi.getBrandingThemes (tenantId);
      (get-request :xero/branding-themes {} t "") 
      :BrandingThemes
      (map #(select-keys % [:BrandingThemeID :Name :Type]))
      (print-table)
       ;(info "xero branding themes: ")
      )

    (->> (get-request :xero/invoice {} t invoice-id)
         (info "invoice: ")
    )  

    (let [qp 
      {:where ;"Date >= DateTime(2022, 01, 01)"
              "(Type == \"ACCREC\")"       
              ;"Date >= DateTime(2022, 01, 01) AND (Type == \"ACCREC\")" ;var invoicesFilter = "Date >= DateTime(" + sevenDaysAgo + ")";
              ; Contact.Name=="Basket Case" AND Type=="ACCREC" AND STATUS=="AUTHORISED"
              ; Type=="BANK" Type=="ASSET"
              ; Status=="VOIDED" OR Status=="DELETED"
              ; Name.Contains("Peter")  Name.StartsWith("") Name.EndsWith("")
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
      }]
      (->> (get-request :xero/invoice qp t "")
           :Invoices
           (map #(select-keys % [:Type :Status :InvoiceNumber  :DateString :Name :Total
           ;  :DueDateString :AmountDue
            ;:InvoiceID :UpdatedDateUTC 
           ]))
           (print-table)
           
           ;(info "invoices: ")
      ))

      (info "invoices modified since: ")
      (let [t (assoc t "If-Modified-Since" "2022-01-01T00:00:00" )
            qp {:where "(Type == \"ACCREC\")"       
                :page 1}]
         (info "header: " t)
         (->> (get-request :xero/invoice qp t "")
               :Invoices
               (map #(select-keys % [:Type :Status :InvoiceNumber  :DateString :Name :Total]))
               (print-table)
          ))






    ))
