(ns modular.oauth2.middleware
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults api-defaults]]
   ;[ring-ttl-session.core :refer [ttl-memory-store]]
  ; [ring.middleware.session :refer [wrap-session]]
  ; [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.oauth2 :refer [wrap-oauth2]]
   [modular.config :refer [get-in-config config-atom]]
   [modular.writer :refer [write-status]]
   [modular.oauth2.provider :refer [ring-oauth2-config]]))

; https://github.com/weavejester/ring-oauth2 

; we currently dont use ring-oauth2 because it makes sense for server-rendered pages
; but we do use the same configuration maps in frontend so we currently just print the
; config that we would use. This is helpful to see if we have client-ids and secrets and
; to se that routes are configured correctly.

(defn print-oauth2-config []
  (let [config @config-atom
        c (ring-oauth2-config config)]
     ;(debug "oauth config: " c)
    (write-status "oauth2" c)))

(defn wrap-oauth [handler]
  (let [config @config-atom
        c (ring-oauth2-config config)]
  ;(wrap-oauth2 handler my-oauth-profiles)
    (-> handler
        (wrap-oauth2 c)
        (wrap-defaults
         (-> site-defaults
             (assoc-in [:security :anti-forgery] false)
            ;(assoc-in [:session :store] (ttl-memory-store (* 60 30)))
             (assoc-in [:session :cookie-attrs :same-site] :lax))))))

; wrap-oauth2 needs to be the first position!
; (defn wrap-base [handler]
;  

(defn wrapx [handler]
  (-> handler
      (wrap-defaults
       (-> site-defaults
           (assoc-in [:security :anti-forgery] false)
            ;(assoc-in [:session :store] (ttl-memory-store (* 60 30)))
           (assoc-in [:session :cookie-attrs :same-site] :lax)))))
