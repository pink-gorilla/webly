(ns rest.pets
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clj-http.client :as http]
   [martian.clj-http :as martian-http]
   [martian.core :as martian]
   [modular.config :refer [get-in-config config-atom]]
   [modular.base64 :refer [base64-encode]]
))


(defn pets []
  (let [swagger-url "https://pedestal-api.herokuapp.com/swagger.json"
        m (martian-http/bootstrap-swagger swagger-url)
        r0 (-> (martian/response-for m :all-pets {}) :body)
         _  (info "all pets: " (pr-str r0))
        pet-id (-> (martian/response-for m :create-pet {:pet {:name "Bongistan"
                                                            :type "Cat"
                                                            :age 33}})
                (get-in [:body :id]))
        _  (info "created pet id: " pet-id)
        r2 (-> (martian/response-for m :get-pet {:id pet-id}) :body)
        _  (info "new pet : " (pr-str r2))

        explore-get-pet (martian/explore m :get-pet)
        _  (info "explore get-pet : " (pr-str explore-get-pet))

        explore-create-pet (martian/explore m :create-pet)
        _  (info "explore create-pet : " (pr-str explore-create-pet))

        explore (martian/explore m)
        _  (info "explore : " (pr-str explore))

        rq (martian/request-for m :create-pet {:pet {:name "Bongistan"
                                                            :type "Cat"
                                                            :age 33}})
        _  (info "create request : " (pr-str rq))
        _ (info "request body: " (slurp (:body rq)))


  
  ]))
