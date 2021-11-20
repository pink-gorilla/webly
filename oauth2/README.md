# modular.oauth2


## credentials setting
- copy profiles/demo/resources/creds-sample.edn to profiles/demo/resources/creds.edn 
- The descripton how to get the credentials is in `profiles/demo/resources/creds-sample.edn`

# develop apps

## github

- scopes: https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
- https://docs.github.com/en/rest/reference/users



# links

; AUTHENTICATION for web apps
; https://github.com/cemerick/friend
; http://ahungry.com/blog/2018-12-26-Clojure-is-Capable.html
https://github.com/ricokahler/oauth2-popup-flow/tree/master/src  we work similar to this library. 
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/fx/auth.cljs
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/views/foursquare.cljs
  https://github.com/liquidz/clj-jwt
https://luminusweb.com/docs/services.html#authentication
https://github.com/funcool/buddy-auth/blob/master/examples/token/src/authexample/web.clj#L58
https://github.com/ovotech/ring-jwt


(defn admin? [req]
  (and (authenticated? req)
       (#{:admin} (:role (:identity req)))))

(def wrap-restricted
  {:name :wrap-restricted
   :wrap (fn wrap-restricted [handler]
           (fn [req]
             (if (boolean (:identity req))
               (handler req)
               (unauthorized
{:error "You are not authorized to perform that action."}))))})


["/restricted"
      {:swagger    {:tags ["restricted"]}
       :middleware [wrap-restricted]}
   ["/user" {:get (fn [request] (ok (-> request :session :identity)))}]]])


