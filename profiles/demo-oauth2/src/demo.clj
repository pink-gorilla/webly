(ns demo
  (:require
   [modular.config :as config]
   [modular.oauth2.store :refer [load-token]]
   [modular.oauth2.request :refer [get-endpoint get-request]]))

(config/set!
 :oauth2
 {:token-path "../../.webly/tokenstore/"})

(load-token :google)

(get-endpoint :google/search)

(get-request :google/search {:q "clojure"
                             :num 10
  ;:cx 4
                             })
(load-token :github)

(get-endpoint :github/userinfo)

(get-request :github/userinfo)

(get-request :github/search-repo {:q "user:pink-gorilla"})




