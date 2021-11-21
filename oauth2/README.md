# modular.oauth2

## credentials setting
- The description how to get the credentials is in `demo-webly/creds-sample.edn`
- create `demo-webly/creds.edn`

# develop apps

## github

- https://docs.github.com/en/rest/reference/users

https://rapidapi.com/category/Finance

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


Thank you! That lead me to the right direction. I found that I could split the token and then get the values from it with that lib: (println (b64/decodeString (second (clojure.string/split token #"\."))))

https://github.com/liquidz/clj-jwt/blob/master/src/clj_jwt/core.clj



[kitchen-async.promise :as p]


https://github.com/liquidz/clj-jwt
https://github.com/liquidz/clj-jwt

Certs from Google.
https://www.googleapis.com/oauth2/v3/certs

https://developers.google.com/identity/sign-in/web/backend-auth#:~:text=After%20you%20receive%20the%20ID,to%20verify%20the%20token's%20signature.