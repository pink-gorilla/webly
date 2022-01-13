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

