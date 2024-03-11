(ns webly.spa.handler.resources
  (:require
    [bidi.ring]
    [modular.webserver.handler.files]))

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
   ;(bidi.ring/->ResourcesMaybe {:prefix "public"})
  (modular.webserver.handler.files/->ResourcesMaybe {:prefix "public"}))

(def file-handler-nodejs
  (modular.webserver.handler.files/->FilesMaybe {:dir "node_modules"}))

(def file-handler-bundel
  (modular.webserver.handler.files/->FilesMaybe {:dir "target/webly/public"}))

(def file-handler-code
  (modular.webserver.handler.files/->ResourcesMaybe {:prefix ""}))




