(ns webly.app.routes
  (:require
   [webly.web.resources :refer [resource-handler file-handler-nodejs file-handler-bundel file-handler-code]]))

(def webly-routes-api
  {"config" {:get :webly/config}
   ; ws
   "token"  :ws/token
   "chsk"  {:get  :ws/chsk-get
            :post :ws/chsk-post}})

(def webly-routes-app
  {;["oauth2/redirect/" :provider] :oauth2/redirect  : either client OR server side
   })

(defn make-routes-frontend [user-routes-app]
  ["/" (merge webly-routes-app user-routes-app)])

(defn make-routes-backend [user-routes-app user-routes-api]
  (let [api-routes (merge webly-routes-api user-routes-api)
        ;app-routes  (merge webly-routes-app user-routes-app)
        ]
    ["/" {"api/"    api-routes
          ; ""       app-routes
          #{"r" "bundel"} file-handler-bundel ;["r"]  ; first from file, thereafter from resource.
          #{"r" "node"} file-handler-nodejs
          #{"r" "jarres"} resource-handler  ;"r"
          "code/" file-handler-code
        ;[true      :webly/not-found]  ; not working as we need to process frontend routes also
          }]))


