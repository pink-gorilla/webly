(ns rest.woo
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [martian.core :as martian]
   [modular.rest.martian.woo :refer [martian-woo]]))

;:method :post
;:body-schema {:pet {:id   s/Int
;                    :name s/Str}}}

(defn woo []
  (let [m (martian-woo)
        r0 (-> (martian/response-for m :orders {}) :body)
         _  (info "orders: " (pr-str r0))
                
        r2 ( -> (martian/response-for m :load-order {:id 139866}) :body)
        _  (info "r2: " (pr-str r2))

        r3  (martian/request-for m :load-order {:id 139866})
        _  (info "request for load-lorder: " (pr-str r3))

        r4 ( -> (martian/response-for m :products {}) :body)
        _  (info "products: " (pr-str r4))

         explore (martian/explore m)
        _  (info "explore : " (pr-str explore))
  
  ]))
