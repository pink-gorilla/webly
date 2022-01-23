(ns rest.validate
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [schema.core :as s]
   [martian.core :as martian]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.rest.martian.xero :refer [martian-xero martian-xero-tenant]]
   [modular.rest.schema.xero :as sxero]))


(defn validate []
  (refresh-auth-token :xero)
  (let [tenant-id "791f3cb4-97b9-45f9-b5e6-7319cda87626"
        t (martian-xero-tenant tenant-id)
        contacts (->> (martian/response-for t :contact-list
                                   {:page 1})
                  :body
                  :Contacts) 
        vr (s/validate [sxero/contact-full] contacts)
        ]
     (info "contacts: " (count contacts))
     (info "validation result: " vr)
     (spit "/tmp/vr.edn" (pr-str vr))

;
  ))