(ns modular.oauth2.token.save-handler
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [ring.util.response :as response]
   [modular.oauth2.token.store :refer [save-token]]))

(defn handler-oauth2-save [req]
  (let [{:keys [token provider]} (get-in req [:body-params])
        res (response/content-type {:status 200
                                    :body nil}
                                   "text/html")]
    (info "saving token for provider: " provider)
    (save-token provider token)
    res
    {:data nil}))
