(ns demo.routes
  (:require
   [webly.web.resources :refer [resource-handler]]
   [webly.oauth2.routes :refer [routes-oauth2]]))

(def demo-routes-api
  {"time"  {:get  :api/time}
   "test"  {:post  :api/test}})

(def demo-routes-app
  {""          :demo/main
   "help"      :demo/help
   "save"      :demo/save ; there is no handler defined for this on purpose
   "oauth2/github/landing" :demo/user})

(def demo-routes-frontend
  ["/" demo-routes-app])

(def demo-routes-backend
  ["/" {""        demo-routes-app
        "api/"    demo-routes-api
        "oauth2/" routes-oauth2
        "r"       resource-handler}])





