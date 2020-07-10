(ns demo.routes)

(def demo-routes-app
  {"" :demo/main
   "help" :demo/help
   "demo/save" :demo/save})

(def demo-routes-api
  {"/api/" {"time"  {:get  :api/time}
            "test"  {:post  :api/test}}})

(def demo-routes-frontend
  ["/" demo-routes-app])

(def demo-routes-backend
  ["" {"/" demo-routes-app
       "" demo-routes-api}])





