# oauth2


## github credentials
  https://github.com/settings/applications/1149314
  github oauth callback needs a URL; this URL is different for prod and dev, so we need different creds 
    ; list your github oauth apps:
    ;https://github.com/settings/applications/
    
    ; register new oauth app
    ; https://github.com/settings/developers
    ; callback url is: localhost:8000/oauth2/github/callback    
    
    ;github oauth callback needs a URL; this URL is different for prod and dev, so we need different creds 

## google credentials
  - https://console.developers.google.com/apis
  - https://console.cloud.google.com/apis/credentials/oauthclient


## credentials setting
- copy profiles/demo/resources/creds-sample.edn to profiles/demo/resources/creds.edn 
- set oauth2 creds


## github
- github oauth2 auth-token endpoint has cors issue.
