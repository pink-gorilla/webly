(ns webly.user.oauth2.routes)

(def routes-oauth2-frontend
  {["redirect/" :provider] :oauth2/redirect})

(def routes-oauth2-api
  {"github/token"  {:get :webly/oauth2-github}})