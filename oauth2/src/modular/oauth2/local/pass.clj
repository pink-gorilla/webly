(ns modular.oauth2.local.pass
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [clojure.set :refer [superset?]]
   [buddy.core.codecs :as codecs]
   [buddy.core.hash :as hash]
   [buddy.sign.jwt :as jwt]
   [no.nsd.clj-jwt :as clj-jwt]
   [modular.config :refer [get-in-config]]))

(defn pwd-hash [pwd]
  (-> (hash/blake2b-128 pwd)
      (codecs/bytes->hex)))

(comment
  (pwd-hash "1234")
  (pwd-hash "7890")
  (pwd-hash "")
;
  )
(defn create-claim [user-name]
  (let [secret (get-in-config [:oauth2 :local :client-secret])
        claim  {:user user-name}
        token (jwt/sign claim secret)]
    (debug "sign claim with secret: " secret)
    {:user user-name
     :token token}))

(defn get-token [user-name user-password]
  (if-let [users (get-in-config [:users])]
    (let [user-kw (keyword user-name)
          password-hashed (pwd-hash user-password)
          user (user-kw users)]
      (if user
        (if (= password-hashed (:password user))
          (create-claim user-name)
          {:error :bad-passowrd
           :error-message (str "Bad password for  [" user-name "].")})
        {:error :user-unknown
         :error-message (str "User [" user-name "] not found.")}))
    {:error :missing-user-config
     :error-message "Please set :users modular.config"}))

(defn verify-token [token]
  (let [secret (get-in-config [:oauth2 :local :client-secret])]
    (debug "verifying claim with secret: " secret)
    (try
      (jwt/unsign token secret)
      (catch Exception _
        {:error :bad-token
         :error-message "Bad Token"}))))

(comment

  (get-token "bongo" "11111")
  (get-token "demo" "11111")
  (get-token "demo" "1234")

  (verify-token "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiZGVtbyJ9.mWO7pjUFhFpEDeQT_3OjM1YCZ1TN8LNZiA_3xF-NkBI")

  (verify-token "eyJhbGciOiJIUzI1NiJ9.eyJ1c2xyIjoiZGVtbyJ9.mWO7pjUFhFpEDeQT_3OjM1YCZ1TN8LNZiA_3xF-NkBI")

  (clj-jwt/str->jwt "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiZGVtbyJ9.mWO7pjUFhFpEDeQT_3OjM1YCZ1TN8LNZiA_3xF-NkBI")

  ; jwks_uri
  ; https://accounts.google.com/.well-known/openid-configuration

  (clj-jwt/unsign
   "https://www.googleapis.com/oauth2/v3/certs"

   ;id-token
   "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQwMWMxYWJlMjQ5MjY5ZjcyZWY3Y2EyNjEzYTg2YzlmMDVlNTk1NjciLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiI4Nzc3MTk3NDczLWQ4cWp1MWsyZTJvMWhrbGZvdWh0MXYwbTM2aW1nYnJ2LmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiODc3NzE5NzQ3My1kOHFqdTFrMmUybzFoa2xmb3VodDF2MG0zNmltZ2Jydi5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsInN1YiI6IjExNzIzMjc5NzY1NjI4MDM5MDg2MSIsImVtYWlsIjoiaG9lcnRsZWhuZXJAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJsOGltUkRtUktfNmdGS0RqNmZWWVlRIiwibm9uY2UiOiIwLjMyMzAxNDQ1MTgzNDgwODgiLCJuYW1lIjoiRmxvcmlhbiBIb2VydGxlaG5lciIsInBpY3R1cmUiOiJodHRwczovL2xoMy5nb29nbGV1c2VyY29udGVudC5jb20vYS9BQVRYQUp4RlRUSGNLZDVGNGVZU2JOcUNUXzZzT2pzdm9YZEVuU29ZTDJDVWEzND1zOTYtYyIsImdpdmVuX25hbWUiOiJGbG9yaWFuIiwiZmFtaWx5X25hbWUiOiJIb2VydGxlaG5lciIsImxvY2FsZSI6ImVuIiwiaWF0IjoxNjQxNTY0OTk0LCJleHAiOjE2NDE1Njg1OTR9.r_yRMMXXyn75-wSucS3OaLbmBA4viX-Pr9_WVxcbbOFuqDAInXYTLHGr9Z0h1hg_IvY_iTM4HSpHUUNv2x82igz4BI7J8q2ZwI4EwTP16i5K5qMAaQ8op4Pk7YrIpFiuH6Ki7zn3eN-Rx1WlORDiPkyYgCQjDr5XSM94EpygiEk2cTFNP0NK0T7XF80CiliWDqkDTuE3sVPWBLab4x0FVfO5M0dLbL70V0Ede2Unb9WbO566xmZv3hqpER0sVHYc1DcNDRetcIVu9RlCccBE18xqTL8tXnsfGWoCO-POilY-1iPEwCj_SLW8u6Rj0ehYHTK96_hHOnBpxuyL8KPGzg"

   ;"1//05HkiJ3mkxEaeCgYIARAAGAUSNwF-L9IrN6ze8eRTdHMXGkTfRe1tQyjUGHAYWyHwmauFf8ZuWPiWTkS4baZCExUWDqzqwzeqdQg"
   ;"ya29.a0ARrdaM93BFZ3FkKkCEPn3acy9INGwmywONA8_TIFgD5YSfx83Tnn6ojGYbJR3rvEv2rZ2htF5SzaVRXvcv2z9pktxavf5Vp9544qr9UbbVTNFQGjZ-vgshyS43oBU15wzmAfki4TBLD2oJypE8PYOpvyWWJi"
   )
; xero
; https://identity.xero.com/.well-known/openid-configuration
  (clj-jwt/unsign
   "https://identity.xero.com/.well-known/openid-configuration/jwks"
   "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFDQUY4RTY2NzcyRDZEQzAyOEQ2NzI2RkQwMjYxNTgxNTcwRUZDMTkiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJISy1PWm5jdGJjQW8xbkp2MENZVmdWY09fQmsifQ.eyJuYmYiOjE2NDE1NjcwOTksImV4cCI6MTY0MTU2ODg5OSwiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS54ZXJvLmNvbSIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHkueGVyby5jb20vcmVzb3VyY2VzIiwiY2xpZW50X2lkIjoiMUQ0RTUxQzMyNDA1NDUxQ0JCQTMyQzExMjkwOUE3QjgiLCJzdWIiOiJkODZhNTIyMThiODk1MDFiODE0ZmIyMDY1YjU5NzNlMSIsImF1dGhfdGltZSI6MTY0MTU2NjQ3OSwieGVyb191c2VyaWQiOiIzYzczNjBjMC02MTk1LTQ2MmQtYjkxMy03NmNlOWM2NmNiYjgiLCJnbG9iYWxfc2Vzc2lvbl9pZCI6IjZjYjZhZjRkNTQ4ZDQ3NDZhZTZjMTNjNWJjOThlOWFmIiwianRpIjoiZTM2Y2NkYzdlMjViOGVlMDFhM2U3YzBkNDAwZDk2OWIiLCJhdXRoZW50aWNhdGlvbl9ldmVudF9pZCI6IjA4ZTg2ZTdiLTZkMjctNDQxMS05MTFiLTY0YjJmMWQ1NzhjMCIsInNjb3BlIjpbImVtYWlsIiwicHJvZmlsZSIsIm9wZW5pZCIsImFjY291bnRpbmcucmVwb3J0cy5yZWFkIiwiYWNjb3VudGluZy5zZXR0aW5ncyIsImFjY291bnRpbmcuYXR0YWNobWVudHMiLCJhY2NvdW50aW5nLnRyYW5zYWN0aW9ucyIsImFjY291bnRpbmcuam91cm5hbHMucmVhZCIsImFjY291bnRpbmcudHJhbnNhY3Rpb25zLnJlYWQiLCJhY2NvdW50aW5nLmNvbnRhY3RzIiwib2ZmbGluZV9hY2Nlc3MiXX0.t9c33xsXXqAfxC8JOyTRPG8b-QrLzqkxIItenXyul3kaSulzue281jed1wFyIpBefDq_xNUfFt4SfrMMyplOxThjQMyYktweyftijfMfnHwa4ZlGJaArdNOFNNzm2XOhdlyjFsVpWrAsMdhb8U9LyZjtagePE90VWyF47N3733tsDj9IBMKOUTg0HVEzyHqR0b-yRXE7KraM9KB3A_-CmuKBjT9JfExfFD8K17vS5T94cHW36EAy1UwWS2NZcFai_nh838Yi4sT1x7HCC3rOJlH8-S-GdmgPXpY5enrJ3nvwhca9bSXQKrnxktubDZeKVV3M1Mfhp5Gr-44Jkzu5Ww"))


