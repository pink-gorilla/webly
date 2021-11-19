(ns modular.oauth2.provider.github)

; :query {code 27aefeb35395d63c34}}
(defn parse-redirect [{:keys [query]}]
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
  {; authorize
   :authorize-uri "https://github.com/login/oauth/authorize"
   :response-type "token"
   :parse-redirect parse-redirect

   ; access token
   :access-token-uri "https://github.com/login/oauth/access_token"
   :parse-dispatch [:github/code->token]
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header auth-header
   :endpoints {:userinfo    "https://api.github.com/user"
               :search-repo "https://api.github.com/search/repositories" ; q=user:USERNAME
               }
   ; userinfo
   :user "https://api.github.com/user"
   :user-parse user-parse})

