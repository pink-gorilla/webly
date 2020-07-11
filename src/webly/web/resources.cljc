(ns webly.web.resources
  (:require
   [clojure.string] ; no empty require for cljs
   #?(:clj [bidi.ring])
   #?(:clj [webly.web.handler :refer [add-ring-handler]])))

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

#?(:clj   (def resource-handler
            (bidi.ring/->ResourcesMaybe {:prefix "public"}))
   :cljs (def resource-handler :webly/resources))

(def routes-resources
  {"/r"  resource-handler ;resources (dedicated path so there is no overlap with api)
   })




