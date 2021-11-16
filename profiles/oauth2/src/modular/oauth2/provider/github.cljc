(ns modular.oauth2.provider.github)

; :query {code 27aefeb35395d63c34}}
(defn parse-github [{:keys [query]}]
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

(defn auth-header [token]
  {"Authorization" (str "token " token)})

(def config
  {:authorize-uri "https://github.com/login/oauth/authorize"
   :access-token-uri "https://github.com/login/oauth/access_token"
   :accessTokenResponseKey "id_token"
   :response-type "token"
   :parse parse-github
   :parse-dispatch [:github/code->token]
   :user "https://api.github.com/user"
   :user-parse user-parse
   :auth-header auth-header})