(ns modular.oauth2.store
  (:require
   [taoensso.timbre :refer [info error]]
   [clojure.java.io :as io]
   [buddy.sign.jwt :as jwt]
   [modular.config :as config]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side-effects
   ))

;; token storage 

(defn filename-token  [name]
  (when-let [token-path (config/get-in-config [:oauth2 :token-path])]
    (str token-path name ".edn")))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn save-token
  [name data]
  (let [filename (filename-token name)]
    (if filename
      (do (ensure-directory (config/get-in-config [:oauth2 :token-path]))
          (save :edn filename data))
      (error "cannot save token - please set [:oauth2 :token-path] in modular config"))))

(defn load-token [name]
  (let [filename (filename-token name)]
    (if filename
      (loadr :edn filename)
      (do
        (error "cannot load token - please set [:oauth2 :token-path] in modular config")
        nil))))

;(defn unsign [token]
;  (with-redefs [buddy.core.codecs (fn [url] {:body "Goodbye world"})]
;    (jwt/unsign token "key")
;    
;  )

(defn validate-token [name]
   ; (jwt/decrypt incoming-data secret)
  (let [token (load-token name)]
    (jwt/unsign token "key") 

    )
   
  )





