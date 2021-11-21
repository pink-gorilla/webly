(ns modular.oauth2.provider
  (:require
   [clojure.string]
   [taoensso.timbre :as timbre :refer [info infof error]]
   [cemerick.url :refer [url url-encode]]
   [modular.oauth2.provider.google :as google]
   [modular.oauth2.provider.github :as github]
   [modular.oauth2.provider.xero :as xero]))

;; our page strucutre for different providers

(defn provider-uri [provider]
  (let [provider-name (name provider)]
    {:launch-uri       (str "/oauth2/auth/" provider-name)
     :redirect-uri     (str "/oauth2/redirect/" provider-name)
     :landing-uri      (str "/oauth2/landing/" provider-name)}))

;; PROVIDER LIST

(def providers
  {:github github/config
   :google google/config
   :xero xero/config})

(defn get-provider-config [p]
  (or (p providers) {}))

;; RING CONFIG

(defn full-provider-config [config provider]
  (let [code (or (get-provider-config provider) {})
        app (or (get-in config [:oauth2 provider]) {})]
    (merge code app)))

(defn ring-oauth2-config [config]
  (let [provider-list (keys providers)
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
               :response_type authorize-response-type
               :scope (scope->string scope)
               ;:nonce (nonce)
               }
        query (if (= provider :xero)
                ; not sure why this is needed.                 
                (assoc query :returnUrl "https://login.xero.com/identity/identity/connect/authorize")
                query)]
    (infof "oauth2 for: %s authorize-uri: %s params: %s" provider authorize-uri (pr-str query))
    (-> (url authorize-uri)
        (assoc :query query)
        str
        ;url-encode
        )))
; https://github.com/login/oauth/authorize?
; client_id=
; &response_type=token
; &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fgithub%2Flanding
; &scope=user%3Aemail%20gist

;  scope=https%3A//www.googleapis.com/auth/drive.metadata.readonly&
 ;include_granted_scopes=true&
; response_type=token&
; state=state_parameter_passthrough_value&
; redirect_uri=https%3A//oauth2.example.com/code&
; client_id=client_id

; http://localhost:8000/oauth2/google/token
; #access_token=ya29.a0AfH6SMBizCNXcAu2WM_P4quZ2Z5z3rHhz0824AO-c_nO2AOiDW7NT3kT3bDNw8wK5i6xMa8ysgKFlwTQv5vvpVCkepmvCGSvm6iwkvVsseaaSOB7Af4uJzX5wbgrZ_4F_6Dkrp9rMO48RtI9Gp2gzvEOqxdT
; &token_type=Bearer
; &expires_in=3599
; &scope=https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly

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