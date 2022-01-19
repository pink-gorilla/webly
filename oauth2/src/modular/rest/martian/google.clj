(ns modular.rest.martian.google
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [martian.core :as martian]
   [schema.core :as s]
   [modular.rest.martian.oauth2 :refer [martian-oauth2]]))

(def endpoints
  [{:route-name :userinfo
    :summary "user info"
    :method :get
    :path-parts ["/oauth2/v2/userinfo"]
   ;:path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :drive-files-list
   ;https://developers.google.com/drive/api/v3/reference/files/list
    :summary "loads files in google drive"
    :method :get
    :path-parts ["/drive/v3/files"]
   ;:path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}
   {:route-name :search
  ; https://www.googleapis.com/customsearch/v1?[parameters]
    :summary "google search"
    :method :get
    :path-parts ["/customsearch/v1"]
    :query-schema {:q s/Str
                   :num s/Num}
   ;:path-schema {:id s/Int}
    :produces ["application/json"]
    :consumes ["application/json"]}])

(def endpoints-sheets
  [{:route-name :sheet-edit
    :summary "edits google sheet"
    :method :put
    :path-parts ["/spreadsheets/" :id :where]
    :path-schema {:id s/Str
                  :where s/Str}
    :query-schema {:valueInputOption s/Str}
    :body-schema {:a {:range s/Str
                      :majorDimension s/Str
                      :values s/Any}}
    :produces ["application/json"]
    :consumes ["application/json"]}])

(def endpoints-search
  [{:route-name :search
    :summary "google search"
    :method :get
    :path-parts ["/v1/search"]
    :query-schema {:q s/Str
                   :num s/Num}
    :produces ["application/json"]
    :consumes ["application/json"]}])

(defn martian-googleapis []
  (let [m (martian-oauth2
           :google
           "https://www.googleapis.com"
           endpoints)]
    m))

(defn martian-google-sheets []
  (let [m (martian-oauth2
           :google
           "https://sheets.googleapis.com/v4"
           endpoints-sheets)]
    m))

(defn martian-google-search []
  (let [m (martian-oauth2
           :google
           "https://api.goog.io"
           endpoints-search)]
    m))

