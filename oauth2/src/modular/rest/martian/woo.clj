(ns modular.rest.martian.woo
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clj-http.client :as http]
   [ajax.core :as ajax]
   [martian.clj-http :as martian-http]
   [martian.core :as martian]
   [martian.interceptors :as interceptors]
   [schema.core :as s]
   [modular.config :refer [get-in-config config-atom]]
   [modular.base64 :refer [base64-encode]]))

(def endpoints
  [{:route-name :orders
    :summary "loads orders"
    :method :get
    :path-parts ["/orders/"]
   ;:path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :load-order
    :summary "loads one order"
    :method :get
    :path-parts ["/orders/" :id]
    :path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :products
    :summary "loads products"
    :method :get
    :path-parts ["/products/"]
    :produces ["application/json"]
    :consumes ["application/json"]}])

(def add-authentication-query-params
  {:name ::add-authentication-query-params
   :enter (fn [ctx]
            (-> ctx
                (assoc-in [:request :query-params :consumer_key] (get-in-config [:oauth2 :woo :consumer-key]))
                (assoc-in [:request :query-params :consumer_secret] (get-in-config [:oauth2 :woo :consumer-secret]))))})

(def interceptors
  {:interceptors
   (concat
    martian/default-interceptors
    [add-authentication-query-params
     interceptors/default-encode-body
     interceptors/default-coerce-response
     martian-http/perform-request])})

(defn base-url []
  (str (get-in-config [:oauth2 :woo :shop-url])
       "/wp-json/wc/v3"))

(defn martian-woo []
  (let [m (martian-http/bootstrap (base-url) endpoints interceptors)]
    m))

