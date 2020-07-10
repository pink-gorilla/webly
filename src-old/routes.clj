(ns goldly.web.routes
  (:require
   [clojure.string]
   [clojure.pprint]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [bidi.ring]
   [goldly.web.handler :refer [app-handler not-found-handler
                               ws-chsk-get ws-chsk-post ws-token-handler
                               handler-auth]]))

; bidi

(defn test-handler [req]
  (clojure.pprint/pprint req)
  {:status 200 :body "test"})

(def routes-bidi
  ["/" {;oauth
        "test"                    (-> #'handler-auth)
        "oauth2/github"           (-> #'handler-auth)
        "oauth2/github/callback"  (-> #'handler-auth)
        "my"                      (-> #'handler-auth)
       ;goldly 
        "app"                   (-> #'app-handler)
        ["system/" :system-id]  (-> #'app-handler)
        
        ;resources (dedicated path so there is no overlap with api)
        "r"                  (bidi.ring/->ResourcesMaybe {:prefix "public"})}
   true                        not-found-handler])

;; todo: (files "/" {:root "./profiles/demo/src/systems"}) ; resources of systems


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

;; WrapMiddleware
;; You can wrap the target handler in Ring middleware as usual. But sometimes
;;  you need to specify that the handlers from certain patterns are wrapped in 
;;  particular middleware.
;; For example :-
;; (match-route ["/index.html" (->WrapMiddleware handler wrap-params)]
;;             "/index.html")
;; Use this with caution. If you are using this you are probably doing it wrong.
;; 
;; Bidi separates URI routing from request handling. Ring middleware is something that should apply to handlers, not routes. If you have a set of middleware common to a group of handlers, you should apply the middleware to each handler in turn, rather than use ->WrapMiddleware. Better to map a middleware applying function over your handlers rather than use this feature.
;;
;; Alternates
;; Sometimes you want to specify a list of potential candidate patterns, which 
;; each match the handler. The first in the list is considered the canonical pattern 
;; for the purposes of URI formation.
;; [#{"/index.html" "/index"} :index]
;; Any pattern can be used in the list. This allows quite sophisticated matching. 
;; For example, if you want to match on requests that are either HEAD or GET but 
;; not anything else.
;;
;; [#{:head :get} :index]
;; Or match if the server name is juxt.pro or localhost.
;; [#{{:server-name "juxt.pro"} {:server-name "localhost"}}
;; {"/index.html" :index}]
;;





