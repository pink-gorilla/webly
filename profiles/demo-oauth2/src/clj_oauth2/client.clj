(ns clj-oauth2.client
  (:refer-clojure :exclude [get])
  (:require [clj-http.client :as http]
            [clojure.string :as str]
          ;  [clojure.tools.logging :as log]
            [uri.core :as uri]
            [clj-http.client :refer [wrap-request]]
            [cheshire.core :as json]
           ; [clj-oauth2.OAuth2Exception]
            )
  (:import [clj_oauth2 OAuth2Exception OAuth2StateMismatchException]
           [org.apache.commons.codec.binary Base64]))

(defn make-auth-request
  [{:keys [authorization-uri client-id redirect-uri scope access-type]}
   & [state]]
  (let [uri (uri/uri->map (uri/make authorization-uri) true)
        query (cond-> (assoc (:query uri)
                             :client_id client-id
                             :redirect_uri redirect-uri
                             :response_type "code")
                state (assoc :state state)
                access-type (assoc :access_type access-type)
                scope (assoc :scope (str/join " " scope)))]
    {:uri (str (uri/make (assoc uri :query query)))
     :scope scope :state state}))

(defn- add-auth-header [req scheme param] ; Force.com
  (let [header (str scheme " " param)]
    (assoc-in req [:headers "Authorization"] header)))

(defn- add-base64-auth-header [req scheme param]
  (add-auth-header req scheme (Base64/encodeBase64String (.getBytes param))))

(defmulti prepare-access-token-request
  (fn [request endpoint params]
    (name (:grant-type endpoint))))

(defmethod prepare-access-token-request "authorization_code"
  [request endpoint params]
  (merge-with merge request
              {:body {:code
                      (:code params)
                      :redirect_uri
                      (:redirect-uri endpoint)}}))

(defmethod prepare-access-token-request "password"
  [request endpoint params]
  (merge-with merge request
              {:body (select-keys params [:username :password])}))

(defn- add-client-authentication [request endpoint]
  (let [{:keys [client-id client-secret authorization-header?]} endpoint]
    (if authorization-header?
      (add-base64-auth-header request "Basic" (str client-id ":" client-secret))
      (merge-with merge request {:body
                                 {:client_id client-id
                                  :client_secret client-secret}}))))

(defn- decode-response
  [{:keys [headers body] :as response}]
  (json/parse-string body true))

(defn- request-access-token
  [endpoint params]
  (let [{:keys [access-token-uri access-query-param grant-type]} endpoint
        request (-> {:content-type "application/x-www-form-urlencoded"
                     :throw-exceptions false
                     :body {:grant_type grant-type}}
                    (prepare-access-token-request endpoint params)
                    (add-client-authentication endpoint)
                    (update :body uri/form-url-encode))
        {:keys [status body] :as response} (http/post access-token-uri request)
        {error :error :as body} (decode-response response)]
    (if (or error (not= status 200))
      (throw (OAuth2Exception. (if error
                                 (if (string? error)
                                   (:error_description body)
                                   (:message error)) ; Facebookism
                                 "error requesting access token")
                               (if error
                                 (if (string? error)
                                   error
                                   (:type error)) ; Facebookism
                                 "unknown")))
      {:access-token (:access_token body)
       :token-type (or (:token_type body) "draft-10") ; Force.com
       :query-param access-query-param
       :params (dissoc body :access_token :token_type)
       :refresh-token (:refresh_token body)})))

(defn get-access-token
  [endpoint & [params {expected-state :state expected-scope :scope}]]
  (let [{:keys [state error]} params]
    (cond (string? error)
          (throw (OAuth2Exception. (:error_description params) error))
          (and expected-state (not= state expected-state))
          (throw (OAuth2StateMismatchException.
                  (format "Expected state %s but got %s"
                          state expected-state)
                  state
                  expected-state))
          :else
          (request-access-token endpoint params))))

(defn with-access-token [uri {:keys [access-token query-param]}]
  (str (uri/make (assoc-in (uri/uri->map (uri/make uri) true)
                           [:query query-param]
                           access-token))))

(defn valid-auth-token?
  "Validated the auth token against the validation URL"
  [url access-token]
  (log/debug "Validating access token" url access-token)
  (let [res (http/get url {:query-params {"access_token" access-token}
                           :throw-exceptions false})]
    (log/debug "Access token validation response" url access-token res)
    (= (:status res) 200)))

(defmulti add-access-token-to-request
  (fn [req oauth2]
    (str/lower-case (:token-type oauth2))))

(defmethod add-access-token-to-request
  :default [req oauth2]
  (let [{:keys [token-type]} oauth2]
    (if (:throw-exceptions req)
      (throw (OAuth2Exception. (str "Unknown token type: " token-type)))
      [req false])))

(defn- make-handler
  [header-name]
  (fn [request {:keys [access-token query-param] :as oauth2}]
    (if access-token
      [(if query-param
         (assoc-in request [:query-params query-param] access-token)
         (add-auth-header request header-name access-token))
       true]
      [request false])))

(defmethod add-access-token-to-request "bearer"
  [request oauth2]
  ((make-handler "Bearer") request oauth2))

(defmethod add-access-token-to-request "draft-10"
  [request oauth2]
  ((make-handler "Oauth") request oauth2))

(defmethod add-access-token-to-request "token"
  [request oauth2]
  ((make-handler "token") request oauth2))

(defn add-userinfo [oauth2-data oauth2-params]
  (let [accesstoken (:access-token oauth2-data)
        userinfo-endpoint (:user-info-uri oauth2-params)
        request {:headers {"Authorization" (str "Bearer " accesstoken)}
                 :content-type "application/json"
                 :throw-exceptions false}
        response (http/get userinfo-endpoint request)
        userinfo (json/parse-string (:body response) true)]
    (assoc oauth2-data :userinfo userinfo)))

(defn wrap-oauth2 [client]
  (fn [req]
    (let [{:keys [oauth2 throw-exceptions]} req
          [req token-added?] (add-access-token-to-request req oauth2)
          req (dissoc req :oauth2)]
      (if token-added?
        (client req)
        (if throw-exceptions
          (throw (OAuth2Exception. "Missing :oauth2 params"))
          (client req))))))

; FIXME: Check how to get refresh token
(defn refresh-access-token
  [refresh-token oauth2-params]
  (let [request (-> {:content-type "application/x-www-form-urlencoded"
                     :throw-exceptions false
                     :form-params
                     {:refresh_token refresh-token
                      :grant_type "refresh_token"}}
                    (add-client-authentication oauth2-params))
        response (http/post (:access-token-uri oauth2-params) request)]
    [(= (:status response) 200) (json/parse-string (:body response) true)]))

(def request
  (wrap-oauth2 http/request))

(defmacro def-request-shortcut-fn [method]
  (let [method-key (keyword method)]
    `(defn ~method [url# & [req#]]
       (request (merge req#
                       {:method ~method-key
                        :url url#})))))

(def-request-shortcut-fn get)
(def-request-shortcut-fn post)
(def-request-shortcut-fn put)
(def-request-shortcut-fn delete)
(def-request-shortcut-fn head)

(comment

  (require
   '[cheshire.core :refer [parse-string]]
   '[clj-oauth2.client :as oauth2])

  (def login-uri
    "https://accounts.google.com")

  (def google-com-oauth2
    {:authorization-uri (str login-uri "/o/oauth2/auth")
     :access-token-uri (str login-uri "/o/oauth2/token")
     :redirect-uri ""
     :client-id ""
     :client-secret ""
     :access-query-param :access_token
     :scope ["https://www.googleapis.com/auth/userinfo.email"]
     :grant-type "authorization_code"
     :access-type "online"
     :approval_prompt ""})

  (def auth-req
    (oauth2/make-auth-request google-com-oauth2))

  ; Visit (:uri auth-req), login and copy code

  (defn google-access-token [code-map]
    (oauth2/get-access-token google-com-oauth2 code-map auth-req))

  (google-access-token
   {:code ""})

  (defn google-user-email [access-token]
    (let [response (oauth2/get "https://www.googleapis.com/oauth2/v1/userinfo" {:oauth2 access-token})]
      (select-keys (parse-string (:body response)) ["email" "verified_email" "picture"])))

  (google-user-email
   {:access-token ""
    :token-type ""}))