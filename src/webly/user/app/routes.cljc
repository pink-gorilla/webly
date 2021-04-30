(ns webly.user.app.routes
  (:require
   [webly.web.resources :refer [resource-handler file-handler]]))

(def webly-routes-api
  {"config" {:get :webly/config}
   "md"     {:get :api/md}
   "oauth2/" {"github/token"  {:get :webly/oauth2-github}
              ;  ["redirect/" :provider] {:get :oauth2/redirect}
              }
   ; ws
   "token"  :ws/token
   "chsk"  {:get  :ws/chsk-get
            :post :ws/chsk-post}})

(def webly-routes-app
  {["md/" :file] :ui/markdown
   ;["oauth2/redirect/" :provider] :oauth2/redirect  : either client OR server side
   })
(defn make-routes-frontend [user-routes-app]
  ["/" (merge webly-routes-app user-routes-app)])

(defn make-routes-backend [user-routes-app user-routes-api]
  (let [api-routes (merge webly-routes-api user-routes-api)
        ;app-routes  (merge webly-routes-app user-routes-app)
        ]
    ["/" {"api/"    api-routes
          ["oauth2/redirect/" :provider] :oauth2/redirect  ;  either client OR server side

       ; ""       app-routes
          "r"       resource-handler
          ["r"]     file-handler

        ;[true      :webly/not-found]  ; not working as we need to process frontend routes also
          }]))


