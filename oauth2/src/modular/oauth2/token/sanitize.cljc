(ns modular.oauth2.token.sanitize
  (:require
   [clojure.set :refer [rename-keys]]))

(defn sanitize-token [token]
  (if (map? token)
    (rename-keys token
                 {:access_token :access-token
                  :refresh_token :refresh-token
                  :id_token :id-token
                  :token_type :token-type
                  :expires_in :expires-in})
    token))