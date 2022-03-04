(ns modular.oauth2.provider
  (:require
   [clojure.string]
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [cemerick.url :refer [url url-encode]]
   [modular.oauth2.provider.google :as google]
   [modular.oauth2.provider.github :as github]
   [modular.oauth2.provider.xero :as xero]
   [modular.oauth2.provider.woo :as woo]
   [modular.oauth2.provider.wordpress :as wordpress]
   [modular.oauth2.protocol :refer [provider-config known-providers]]))

;; our page strucutre for different providers

(defn provider-uri [provider]
  (let [provider-name (name provider)]
    {:start-uri       (str "/api/oauth2/start/" provider-name)
     :redirect-uri     (str "/oauth2/redirect/" provider-name)
     :landing-uri      (str "/oauth2/landing/" provider-name)}))

;; PROVIDER LIST

(defn get-provider-config [p]
  (let [c (provider-config p)]
    (debug "provider " p "config: " c)
    c))

;; RING CONFIG

(defn full-provider-config [config provider]
  (let [code (or (get-provider-config provider) {})
        app (or (get-in config [:oauth2 provider]) {})]
    (merge code app)))

(defn ring-oauth2-config [config]
  (let [provider-list (known-providers)
        list (map (fn [p] {p (full-provider-config config p)}) provider-list)]
    (apply merge list)))

;; AUTHORIZE - START

(defn url-start [provider-kw] ;current-url
  (->> provider-kw
       provider-uri
       :start-uri
       ;(set-relative-path current-url)
       ))
(defn scope->string [scope]
  (let [scope (if (nil? scope) "" scope)
        scope (if (string? scope)
                scope
                (clojure.string/join " " scope))]
    scope))

#?(:cljs (defn nonce []
           (.toString (.random js/Math)))
   :clj  (defn nonce []
           (str (rand-int Integer/MAX_VALUE))))

(defn set-relative-path [current-url path]
  (-> (url current-url)
      (assoc :path path)
      (.toString)))

;(defn url-without-qp [url-str]
;  (let [{:keys [proto host port path]} (url url-str)
;        port-str (if (< 0 port)
;                   (str ":" port)
;                   "")
;        url-str-no-qp (str proto ":" host port-str path)]
;    (info "url without qp: " url-str-no-qp)
;    url-str-no-qp))

(defn url-without-qp [url-str]
  (info "url with qp: " url-str)
  (let [url-no-qp-str (-> (url url-str)
                          (assoc :query nil :anchor nil)
                          str)]
    (info "url without qp: " url-no-qp-str)
    url-no-qp-str))

(defn url-redirect [provider-kw current-url]
  (->> provider-kw
       provider-uri
       :redirect-uri
       (set-relative-path current-url)
       (url-without-qp)))

(defn url-authorize [config provider current-url]
  (let [{:keys [authorize-uri client-id scope
                authorize-query-params
                authorize-nonce authorize-redirect-uri-name]} (full-provider-config config provider)
        query {:client_id client-id
               :redirect_uri  (url-redirect provider current-url)
               ; If the value is code, launches a Basic authorization code flow, requiring a POST to the token endpoint to obtain the tokens.
               ;  If the value is token id_token or id_token token, launches an Implicit flow
               ;:response_type authorize-response-type ; response_type=token
               :scope (scope->string scope)
               ; state: sessionid
               }
        query (if authorize-redirect-uri-name
                (rename-keys query {:redirect_uri authorize-redirect-uri-name})
                query)
        query (if authorize-nonce
                (assoc query :nonce (nonce))
                query)
        query (merge authorize-query-params query)]
    (info "authorize query params: " authorize-query-params)
    (infof "oauth2 for: %s authorize-uri: %s params: %s" provider authorize-uri (pr-str query))
    (-> (url authorize-uri)
        (assoc :query query)
        str
        ;url-encode
        )))

;; REQUESTS (use the api)

(defn get-provider-auth-header [p token]
  (if-let [config (get-provider-config p)]
    (let [auth-header (:auth-header config)]
      (auth-header token))
    (do (error "cannot get auth header for unknwon provider")
        {})))

(defn parse-userinfo [p token]
  (if-let [config (get-provider-config p)]
    (let [user-parse (:user-parse config)]
      (user-parse token))
    (do (error "cannot parse userinfo unknwon provider")
        {})))