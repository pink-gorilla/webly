(ns rest.wordpress
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
  
))


;; this does not work. 


; If you are trying to view a protected endpoint (products, orders, users, etc), 
; the access token MUST belong to a user that has “manager” capabilities. 
; Currently, there is no workaround for this and is a limitation/restriction put
; in place by WooCommerce.

; Due to the way the REST API was written, scopes serve little purpose with 
; using the default REST API routes. WordPress simply looks at users permissions 
; via capabilities and processes the request.


(defn wordpress []

#_(->> (get-request :wordpress/userinfo)
       pr-str
       (info "wordpress/userinfo: "))

  (->> (get-request :wordpress/posts)
       pr-str
       (info "wordpress/posts: "))

  #_(->> (get-request :wordpress/woo-data)
       pr-str
       (info "wordpress/woo-data: "))



    #_(->> (get-request :wordpress/products)
       pr-str
       (info "wordpress/products: "))


   (->> (get-request :wordpress/orders)
       pr-str
       (info "wordpress/orders: "))


  )