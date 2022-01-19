(ns modular.oauth2.provider.github
  (:require
   [modular.oauth2.protocol :refer [provider-config]]))

; :query {code 27aefeb35395d63c34}}
(defn parse-authorize-response [{:keys [query]}]
  {:code (:code query)})

  ; :github {:email "name@domain.com"
;          :login "masterbuilder99"
;          :id 6767676
;          :public_gists 4
;          :public_repos 1
;          :created_at "2015-05-27T14:46:29Z"
;          :avatar_url "https://avatars.githubusercontent.com/u/82429483?v=4"}

(defn user-parse [data]
  {:user (:login data)
   :email (:email data)})

(defn api-request-auth-header [token]
  {"Authorization" (str "token " token)})

(def config
  {; authorize
   :authorize-uri "https://github.com/login/oauth/authorize"
   :authorize-query-params {:response_type "code" ;  "token"
                            }

   :parse-authorize-response parse-authorize-response

   ; token
   :token-uri "https://github.com/login/oauth/access_token"
   :parse-dispatch [:github/code->token]
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header api-request-auth-header
   :endpoints {:userinfo    "https://api.github.com/user"}
   ; userinfo
   :user "https://api.github.com/user"
   :user-parse user-parse
   :icon  "fab fa-github-square"})

(defmethod provider-config :github [_]
  config)



