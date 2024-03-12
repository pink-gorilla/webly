(ns webly.spa.handler.routes
  (:require
   [modular.webserver.handler.files :refer [->ResourcesMaybe ->FilesMaybe]]
   [webly.spa.handler.routes-resolve :refer [resolve-handler]]))

(defn make-routes-frontend [user-routes-app]
  ["/" user-routes-app])

;; from: 
;; https://github.com/juxt/bidi/blob/master/README.md
;; 
;; The Resources and ResourcesMaybe record can be used on the right-hand 
;; side of a route. It serves resources from the classpath. After the 
;; pattern is matched, the remaining part of the path is added to the given prefix.
;; ["/resources" (->ResourcesMaybe {:prefix "public/"})
;; There is an important difference between Resources and ResourcesMaybe. 
;; Resources will return a 404 response if the resource cannot be found, while
;;  ResourcesMaybe will return nil, allowing subsequent routes to be tried.

(def resource-handler 
  {#{"r" "bundel"} (->FilesMaybe {:dir "target/webly/public"})
   #{"r" "node"}   (->FilesMaybe {:dir "node_modules"})
   #{"r" "jarres"} (->ResourcesMaybe {:prefix "public"})
   "code/"  (->ResourcesMaybe {:prefix ""})})


(defn make-routes-backend [user-routes-api config-route websocket-routes]
  (let [api-routes (merge config-route websocket-routes user-routes-api)
        api-routes (resolve-handler api-routes)
        ]
    ["/" {"api/"    api-routes
          ; ""       app-routes
          "" resource-handler
        ;[true      :webly/not-found]  ; not working as we need to process frontend routes also
          }]))


