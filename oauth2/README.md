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


(:refer-clojure :exclude [get])

(defn http-basic-deny
  [realm request]
  {:status 401
   :headers {"Content-Type" "text/plain"
             "WWW-Authenticate" (format "Basic realm=\"%s\"" realm)}})


Ring-jwt rocks!


; return the 401 only for unauthenticated requests and use an HTTP 403 Forbidden instead for requests that to not have adequate authorization,


https://github.com/cjohansen/auth0-ring/tree/master/src/auth0_ring





of multiple endpoints for authenticating users, and for requesting resources including tokens, user information, and public keys.

To simplify implementations and increase flexibility, OpenID Connect allows the use of a "Discovery document," a JSON document found at a well-known location containing key-value pairs which provide details about the OpenID Connect provider's configuration, including the URIs of the authorization, token, revocation, userinfo, and public-keys endpoints. The Discovery document for Google's OpenID Connect service may be retrieved from:

https://accounts.google.com/.well-known/openid-configuration

To use Google's OpenID Connect services, you should hard-code the Discovery-document URI (https://accounts.google.com/.well-known/openid-configuration) into your application. Your application fetches the document, applies caching rules in the response, then retrieves endpoint URIs from it as needed. For example, to authenticate a user, your code would retrieve the authorization_endpoint metadata value (https://accounts.google.com/o/oauth2/v2/auth in the example below) as the base URI for authentication requests that are sent to Google.

Here is an example of such a document; the field names are those specified in OpenID Connect Discovery 1.0 (refer to that document for their meanings). The values are purely illustrative and might change, although they are copied from from a recent version of the actual Google Discovery document:

{
  "issuer": "https://accounts.google.com",
  "authorization_endpoint": "https://accounts.google.com/o/oauth2/v2/auth",
  "device_authorization_endpoint": "https://oauth2.googleapis.com/device/code",
  "token_endpoint": "https://oauth2.googleapis.com/token",
  "userinfo_endpoint": "https://openidconnect.googleapis.com/v1/userinfo",
  "revocation_endpoint": "https://oauth2.googleapis.com/revoke",
  "jwks_uri": "https://www.googleapis.com/oauth2/v3/certs",
  "response_types_supported": [
    "code",
    "token",
    "id_token",
    "code token",
    "code id_token",
    "token id_token",
    "code token id_token",
    "none"
  ],
  "subject_types_supported": [
    "public"
  ],
  "id_token_signing_alg_values_supported": [
    "RS256"
  ],
  "scopes_supported": [
    "openid",
    "email",
    "profile"
  ],
  "token_endpoint_auth_methods_supported": [
    "client_secret_post",
    "client_secret_basic"
  ],
  "claims_supported": [
    "aud",
    "email",
    "email_verified",
    "exp",
    "family_name",
    "given_name",
    "iat",
    "iss",
    "locale",
    "name",
    "picture",
    "sub"
  ],
  "code_challenge_methods_supported": [
    "plain",
    "S256"
  ]
}

Google ID Tokens may contain the following fields (known as claims):
Claim 	Provided 	Description
aud 	always 	The audience that this ID token is intended for. It must be one of the OAuth 2.0 client IDs of your application.
exp 	always 	Expiration time on or after which the ID token must not be accepted. Represented in Unix time (integer seconds).
iat 	always 	The time the ID token was issued. Represented in Unix time (integer seconds).
iss 	always 	The Issuer Identifier for the Issuer of the response. Always https://accounts.google.com or accounts.google.com for Google ID tokens.
sub 	always 	An identifier for the user, unique among all Google accounts and never reused. A Google account can have multiple email addresses at different points in time, but the sub value is never changed. Use sub within your application as the unique-identifier key for the user. Maximum length of 255 case-sensitive ASCII characters.
at_hash 		Access token hash. Provides validation that the access token is tied to the identity token. If the ID token is issued with an access_token value in the server flow, this claim is always included. This claim can be used as an alternate mechanism to protect against cross-site request forgery attacks, but if you follow Step 1 and Step 3 it is not necessary to verify the access token.
azp 		The client_id of the authorized presenter. This claim is only needed when the party requesting the ID token is not the same as the audience of the ID token. This may be the case at Google for hybrid apps where a web application and Android app have a different OAuth 2.0 client_id but share the same Google APIs project.
email 		The user's email address. This value may not be unique to this user and is not suitable for use as a primary key. Provided only if your scope included the email scope value.
email_verified 		True if the user's e-mail address has been verified; otherwise false.
family_name 		The user's surname(s) or last name(s). Might be provided when a name claim is present.
given_name 		The user's given name(s) or first name(s). Might be provided when a name claim is present.
hd 		The hosted G Suite domain of the user. Provided only if the user belongs to a hosted domain.
locale 		The user's locale, represented by a BCP 47 language tag. Might be provided when a name claim is present.
name 		The user's full name, in a displayable form. Might be provided when:

    The request scope included the string "profile"
    The ID token is returned from a token refresh

When name claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.
nonce 		The value of the nonce supplied by your app in the authentication request. You should enforce protection against replay attacks by ensuring it is presented only once.
picture 		The URL of the user's profile picture. Might be provided when:

    The request scope included the string "profile"
    The ID token is returned from a token refresh

When picture claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.
profile 		The URL of the user's profile page. Might be provided when:

    The request scope included the string "profile"
    The ID token is returned from a token refresh

When profile claims are present, you can use them to update your app's user records. Note that this claim is never guaranteed to be present.


An error response:

https://oauth2.example.com/auth?error=access_denied

An authorization code response:

https://oauth2.example.com/auth?code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7