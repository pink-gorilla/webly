(ns webly.user.oauth2.provider
  (:require
   [taoensso.timbre :as timbre :refer [info infof error]]
   [cemerick.url :refer [url url-encode]]))


;{"token_type" "Bearer", 
;"access_token" "ya29.a0AfH6SMD4K4sFxQ8LaWVOv_gPteXEdmGjRIFlWXxtJ7z0trs5p6bpoNmQ3ebN", 
;"scope" "https://www.googleapis.com/auth/drive.readonly https://www.googleapis.com/auth/spreadsheets.readonly",
; "expires_in" "3599"}


(defn parse-google [{:keys [anchor]}]
  {:access-token (:access_token anchor)
   :scope (:scope anchor)
   :expires #?(:cljs (js/parseInt (:expires_in anchor))
               :clj (:expires_in anchor))})

; :query {code 27aefeb35395d63c34}}
(defn parse-github [{:keys [query]}]
  {:code (:code query)})


;{:scope "openid profile"
;   :responseType "id_token"
;   :accessTokenResponseKey "id_token"


(def providers
  {:github {:authorize-uri "https://github.com/login/oauth/authorize"
            :access-token-uri "https://github.com/login/oauth/access_token"
            :accessTokenResponseKey "id_token"
            :response-type "token"
            :parse parse-github
            :parse-dispatch [:github/code->token]}
   :google {:authorize-uri "https://accounts.google.com/o/oauth2/v2/auth"
            :access-token-uri "https://accounts.google.com/o/oauth2/v2/access_token"
            :response-type "token"
            :accessTokenResponseKey "id_token"
            :parse parse-google}})

; :oauth2 {:github {:clientId        ""
;                   :clientSecret    ""
;                   :scope ""}
;          :google {:clientId        ""
;                   :clientSecret    ""
;                   :scope ""}}

(defn provider-uri [provider]
  (let [provider-name (name provider)]
    {:launch-uri       (str "/oauth2/auth/" provider-name)
     :redirect-uri     (str "/oauth2/redirect/" provider-name)
     :landing-uri      (str "/oauth2/landing/" provider-name)}))

(defn safe-scope [scope]
  (let [scope (if (nil? scope) "" scope)
        scope (if (string? scope)
                scope
                (clojure.string/join " " scope))]
    scope))

(defn get-provider [config provider]
  (let [p-code (or (provider providers) {})
        p-config (or (get-in config [:oauth2 provider]) {})
        p-uri (provider-uri provider)
        p (merge p-uri p-code p-config)]
    (assoc p :scopes (safe-scope (:scopes p)))
      ;p
    ))

(defn ring-oauth2-config [config]
  (let [provider-list (keys providers)
        list (map (fn [p] {p (get-provider config p)}) provider-list)]
    (apply merge list)))

(defn current-path [current-url path]
  (-> (url current-url)
      (assoc :path path)
      (.toString)))

; https://github.com/login/oauth/authorize?
; client_id=
; &response_type=token
; &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Foauth2%2Fgithub%2Flanding
; &scope=user%3Aemail%20gist

#?(:cljs (defn nonce []
           (.toString (.random js/Math)))
   :clj  (defn nonce []
           (str (rand-int Integer/MAX_VALUE))))

(defn url-authorize [config provider current-url]
  (let [{:keys [authorize-uri redirect-uri scopes client-id response-type]} (get-provider config provider)
        redirect-uri (current-path current-url redirect-uri)
        query {:client_id client-id
               :redirect_uri redirect-uri
               :response_type response-type
               :scope scopes
               ;:nonce (nonce)
               }]
    (infof "oauth2 for: %s authorize-uri: %s callback-uri: %s" provider authorize-uri redirect-uri)
    (-> (url authorize-uri)
        (assoc :query query)
        str
        ;url-encode
        )))


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

