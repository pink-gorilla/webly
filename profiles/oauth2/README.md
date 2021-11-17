# modular.oauth2


## credentials setting
- copy profiles/demo/resources/creds-sample.edn to profiles/demo/resources/creds.edn 
- The descripton how to get the credentials is in `profiles/demo/resources/creds-sample.edn`

# develop apps

## github

- scopes: https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
- https://docs.github.com/en/rest/reference/users

## google

'https://www.googleapis.com/auth/spreadsheets',
            'https://www.googleapis.com/auth/cloud-platform',
            //'https://www.googleapis.com/auth/calendar'

            'https://www.googleapis.com/auth/drive',
            'https://www.googleapis.com/auth/drive.appdata',
            'https://www.googleapis.com/auth/drive.file',
            'https://www.googleapis.com/auth/drive.metadata',
            'https://www.googleapis.com/auth/drive.metadata.readonly',
            'https://www.googleapis.com/auth/drive.photos.readonly',
            'https://www.googleapis.com/auth/drive.readonly'
            https://www.googleapis.com/auth/gmail.readonly

# links

; AUTHENTICATION for web apps
; https://github.com/cemerick/friend
; http://ahungry.com/blog/2018-12-26-Clojure-is-Capable.html

https://github.com/ricokahler/oauth2-popup-flow/tree/master/src
we work similar to this library. 

;; stolen from
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/fx/auth.cljs
;; https://github.com/timgilbert/haunting-refrain-posh/blob/develop/src/cljs/haunting_refrain/views/foursquare.cljs




https://luminusweb.com/docs/services.html#authentication

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