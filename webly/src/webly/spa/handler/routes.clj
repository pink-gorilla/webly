(ns webly.spa.handler.routes
  (:require
   [webly.spa.handler.resources :refer [resource-handler file-handler-nodejs file-handler-bundel file-handler-code]]
   [webly.spa.handler.routes-resolve :refer [resolve-handler]]
   ))

(defn make-routes-frontend [user-routes-app]
  ["/" user-routes-app ;(merge webly-routes-app user-routes-app)
  ])


(defn make-routes-backend [user-routes-api config-route websocket-routes]
  (let [api-routes (merge config-route websocket-routes user-routes-api)
        api-routes (resolve-handler api-routes)
        ]
    ["/" {"api/"    api-routes
          ; ""       app-routes
          #{"r" "bundel"} file-handler-bundel ;["r"]  ; first from file, thereafter from resource.
          #{"r" "node"} file-handler-nodejs
          #{"r" "jarres"} resource-handler  ;"r"
          "code/" file-handler-code
        ;[true      :webly/not-found]  ; not working as we need to process frontend routes also
          }]))


