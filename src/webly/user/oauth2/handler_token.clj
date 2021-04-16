(ns webly.user.oauth2.handler-token
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [ring.util.response :as res]
   [ajax.core :as ajax]
   [webly.config :refer [get-in-config config-atom]]
   [webly.web.handler :refer [add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]
   [webly.user.oauth2.provider :refer [get-provider]]))

(defn handler-github-redirect [req]
  (let [p (promise)
        prov (get-provider @config-atom :github)
        {:keys [access-token-uri client-id client-secret]} prov
        code (get-in req [:params :code])]
    (info "getting github access token for code :" code "client-id:" client-id)
    (ajax/POST access-token-uri ; "https://github.com/login/oauth/access_token"
      :params {:client_id	 client-id
               :client_secret client-secret
               :code code}
      :format (ajax/json-request-format) ;  {:keywords? false})
      :timeout 25000                     ;; optional see API docs
      :response-format (ajax/json-response-format); {:keywords? true})
      :handler (fn [res]
                 (info "github access-token success: " res)
                 (deliver p res))
      :error-handler (fn [res]
                       (error "github access-token code " code " error: " res)
                       (deliver p res)))

    (res/response @p)))

(comment
  (handler-github-redirect {})

  ;
  )

(def handler-github-redirect-wrapped
  (-> handler-github-redirect
      wrap-api-handler))

(add-ring-handler :webly/oauth2-github handler-github-redirect-wrapped)