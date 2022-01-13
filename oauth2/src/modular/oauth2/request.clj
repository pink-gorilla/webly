(ns modular.oauth2.request
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [clojure.data.json :as json]
   [cemerick.url :refer [url url-encode]]
   [clj-http.client :as http]
   [clojure.string :as string]
   [cheshire.core]
   [modular.oauth2.provider :refer [get-provider-config get-provider-auth-header]]
   [modular.oauth2.token.store :refer [load-token]]))

(defn get-auth-header
  [provider]
  (let [config (get-provider-config provider)
        token (load-token provider)
        access-token (:access-token token)
        header (get-provider-auth-header provider access-token)]
    header))

(defn get-endpoint
  [provider_endpoint]
  (let [provider (-> provider_endpoint namespace keyword)
        endpoint (-> provider_endpoint name keyword)
        config (get-provider-config provider)
        url (get-in config [:endpoints endpoint])
        token (load-token provider)
        at (:access-token token);; Xero example for authroize request
        header (get-provider-auth-header provider at)]
    {:provider provider
     :endpoint endpoint
     :url url
     :token token
     :header header}))

(defn get-request
  ([provider_endpoint]
   (get-request provider_endpoint {}))
  ([provider_endpoint query-params]
   (info "get-request " provider_endpoint query-params)
   (let [{:keys [url header]} (get-endpoint provider_endpoint)]
     (debug "http/get " url)
     (debug "headers: " (pr-str header))
     (try
       (-> (http/get url {:headers header
                          :accept :json
                          :query-params query-params})
           (:body)
           (cheshire.core/parse-string true))
       (catch clojure.lang.ExceptionInfo e
         (error "get-request " provider_endpoint " error: " (.getMessage e))
         ;(warn (.getData e))
        ; (info (prn e))
         (when-let [body (-> e .getData :body)]
           (let [b (cheshire.core/parse-string body true)]
             (warn "body: " b)
             b)))
       (catch Exception e
         (error "get-request " provider_endpoint " exception")
         ;(error e)
         (error "keys of error: " (keys e))
         ;;(error (:body e))
         )))))

(defn get-request-xero
  ([tenant-id provider_endpoint]
   (get-request provider_endpoint {}))
  ([tenant-id provider_endpoint query-params]
   (info "get-request " provider_endpoint query-params)
   (let [{:keys [url header]} (get-endpoint provider_endpoint)]
     (info "http/get " url)
     (-> (http/get url {:headers (assoc header "Xero-Tenant-Id" tenant-id)
                        :accept :json
                        :query-params query-params})
         (:body)
         (cheshire.core/parse-string true)))))

(defn post-request
  ([provider url body-params]
   (info "post-request " provider url body-params)
   (let [header (get-auth-header provider)]
     (debug "http/get " url)
     (info "htttp/post url: " url " header: " (pr-str header))
     (try
       (-> (http/post url {:headers header
                           :accept :json
                           :body (cheshire.core/generate-string body-params)
                           :content-type :json})
           (:body)
           (cheshire.core/parse-string true))
       (catch clojure.lang.ExceptionInfo e
         (error "post-request " url " error: " (.getMessage e))
         ;(warn (.getData e))
        ; (info (prn e))
         (when-let [body (-> e .getData :body)]
           (warn "body: " body)
           (let [b (cheshire.core/parse-string body true)]
             (warn "body: " b)
             b)))
       (catch Exception e
         (error "post-request " provider " exception")
         ;(error e)
         (error "keys of error: " (keys e))
         ;;(error (:body e))
         )))))
(defn put-request
  ([provider url body-params]
   (info "put-request " provider url body-params)
   (let [header (get-auth-header provider)]
     (debug "http/put " url)
     (info "htttp/put url: " url " header: " (pr-str header))
     (try
       (-> (http/put url {:headers header
                          :accept :json
                          :body (cheshire.core/generate-string body-params)
                          :content-type :json})
           (:body)
           (cheshire.core/parse-string true))
       (catch clojure.lang.ExceptionInfo e
         (error "put-request " url " error: " (.getMessage e))
         ;(warn (.getData e))
        ; (info (prn e))
         (when-let [body (-> e .getData :body)]
           (warn "body: " body)
           (let [b (cheshire.core/parse-string body true)]
             (warn "body: " b)
             b)))
       (catch Exception e
         (error "put-request " provider " exception")
         ;(error e)
         (error "keys of error: " (keys e))
         ;;(error (:body e))
         )))))
(comment

  (get-endpoint :google/userinfo)

  (-> (get-endpoint :google/userinfo)
      (get-in [:header "Authorization"]))

  (get-request :google/userinfo)

  (get-request :github/userinfo)

;  
  )