(ns modular.rest.martian.github
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [martian.core :as martian]
   [schema.core :as s]
   [modular.rest.martian.oauth2 :refer [martian-oauth2]]))

(def endpoints
  [{:route-name :userinfo
    :summary "user info"
    :method :get
    :path-parts ["/user"]
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :search-repo
   ;https://developers.google.com/drive/api/v3/reference/files/list
    :summary "search repositories"
    :method :get
    :path-parts ["/search/repositories"]
   ;:path-schema {:id s/Int}
    :query-schema {:q s/Str}
    :produces ["application/json"]
    :consumes ["application/json"]}])

(defn martian-github []
  (let [m (martian-oauth2
           :github
           "https://api.github.com"
           endpoints)]
    m))

