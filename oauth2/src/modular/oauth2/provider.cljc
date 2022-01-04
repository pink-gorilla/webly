(ns modular.oauth2.provider
  (:require
   [clojure.string]
   [taoensso.timbre :as timbre :refer [info infof error]]
   [cemerick.url :refer [url url-encode]]
   [modular.oauth2.provider.google :as google]
   [modular.oauth2.provider.github :as github]
   [modular.oauth2.provider.xero :as xero]
   [modular.oauth2.protocol :refer [provider-config known-providers]]))

;; our page strucutre for different providers

(defn provider-uri [provider]
  (let [provider-name (name provider)]
    {:launch-uri       (str "/oauth2/auth/" provider-name)
     :redirect-uri     (str "/oauth2/redirect/" provider-name)
     :landing-uri      (str "/oauth2/landing/" provider-name)}))

;; PROVIDER LIST

(defn get-provider-config [p]
  (let [c (provider-config p)]
    (info "provider " p "config: " c)
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

(defn url-redirect [provider-kw current-url]
  (->> provider-kw
       provider-uri
       :redirect-uri
       (set-relative-path current-url)))

(defn url-authorize [config provider current-url]
  (let [{:keys [authorize-uri authorize-response-type client-id scope]} (full-provider-config config provider)
        query {:client_id client-id
               :redirect_uri  (url-redirect provider current-url)
               ; If the value is code, launches a Basic authorization code flow, requiring a POST to the token endpoint to obtain the tokens.
               ;  If the value is token id_token or id_token token, launches an Implicit flow
               :response_type authorize-response-type ; response_type=token
               :scope (scope->string scope)
               ; state: sessionid
               }
        query (case provider
                :xero (assoc query :returnUrl "https://login.xero.com/identity/identity/connect/authorize") ; not sure why this is needed.                 
                :google
                (assoc query :access_type "offline"; the client does not receive a refresh token unless a value of offline is specified. (online or offline
                       :nonce (nonce))
                query)]
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