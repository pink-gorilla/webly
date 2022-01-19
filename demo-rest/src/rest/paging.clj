(ns rest.paging
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.rest.paging :refer [iteration request-paginated]]
   [modular.rest.martian.xero :refer [martian-xero martian-xero-tenant]]
   ))

(defn paging-test []
  (let [items 12 
        pgsize 5
        src (vec (repeatedly items #(java.util.UUID/randomUUID)))
        api (fn [tok]
              (let [tok (or tok 0)]
                (when (< tok items)
                  {:tok (+ tok pgsize)
                   :ret (subvec src tok (min (+ tok pgsize) items))})))
        results  (mapcat identity (iteration api :kf :tok :vf :ret))
                   ]
    ;(is (= src
    ;     (mapcat identity (iteration api :kf :tok :vf :ret))
    ;     (into [] cat (iteration api :kf :tok :vf :ret)))
    ;     )

    (info "iterated results:" (pr-str results)) 
;
    ))

(defn print-invoices [invoices]
  (->> invoices
      (map #(select-keys % [:Type :Status :InvoiceNumber  :DateString :Name :Total
            ;  :DueDateString :AmountDue
             ;:InvoiceID :UpdatedDateUTC 
            ]))
       (print-table)))

(defn paging-xero []
  (refresh-auth-token :xero)
  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"
        t (martian-xero-tenant tenant-id)
        params {:where "(Type == \"ACCREC\")"} ;"Date >= DateTime(2022, 01, 01)"
        result (request-paginated t :invoice-list params :Invoices)
       ]
   ; (print-invoices result)
    (info "inv count: " (count result))
  ))

