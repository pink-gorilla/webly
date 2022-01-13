(ns modular.oauth2.token.info
  (:require
   [modular.oauth2.token.store :refer [load-token]]
   [buddy.sign.jwt :as jwt]
   [clj-jwt.core :refer [str->jwt]]
   [no.nsd.clj-jwt :as clj-jwt]))

(defn get-token [p kw]
  (-> (load-token p)
      kw ; :access-token
      str->jwt))

(defn get-header [p kw]
  (-> p (get-token kw) :header))

(defn get-claims [p kw]
  (-> p (get-token kw) :claims))

(defn get-signature [p kw]
  (-> p (get-token kw) :signature))

(defn get-encoded-data [p kw]
  (-> p (get-token kw) :encoded-data))


(defn token-summary [p]
  (let [token (load-token p)
        id-token (:id-token token)
        id (when id-token 
               (-> id-token 
                   str->jwt
                   :claims
                   :email))]
    {:provider p
     :available (if token true false)
     :id id}))

(defn tokens-summary [providers]
   (into []
      (map token-summary providers)))


(defn- token-summary-vec [p]
  [p (token-summary p)])


(defn tokens-summary-map [providers]
   (into {}
      (map token-summary-vec providers)))
      