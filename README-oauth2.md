# oauth2

# credentials generation (clientId + clientSecret)

## github credentials

  github oauth callback needs a URL; this URL is different for prod and dev, so we need different creds 

  https://github.com/settings/applications/1149314

    ; list your github oauth apps:
    ;https://github.com/settings/applications/
    
    ; register new oauth app
    ; https://github.com/settings/developers
    ; callback url is: localhost:8000/oauth2/github/callback    
    
## google credentials
  - https://console.developers.google.com/apis
  - https://console.cloud.google.com/apis/credentials/oauthclient

## credentials setting
- copy profiles/demo/resources/creds-sample.edn to profiles/demo/resources/creds.edn 
- set oauth2 creds

# develop apps

## github
- github oauth2 auth-token endpoint has cors issue. therefore ring handler with auth endpoint exists
- scopes: https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
- https://docs.github.com/en/rest/reference/users
- github oauth callback needs a URL; this URL is different for prod and dev, so we need different creds
- ;:redirectUri "http://localhost:8000/oauth2/google/token"

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



--