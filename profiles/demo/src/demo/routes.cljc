(ns demo.routes
  (:require
   [webly.web.resources :refer [resource-handler]]))

(def demo-routes-api
  {"time"  {:get  :api/time}
   "test"  {:post  :api/test}})

(def demo-routes-app
  {""          :demo/main
   "help"      :demo/help
   "save"      :demo/save ; there is no handler defined for this on purpose
   })

(def demo-routes-frontend
  ["/" demo-routes-app])

(def demo-routes-backend
  ["/" {"" demo-routes-app
        "api/" demo-routes-api
        "r" resource-handler}])





