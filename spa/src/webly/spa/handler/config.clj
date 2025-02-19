(ns webly.spa.handler.config
  (:require
   [ring.util.response :refer [response]]))


(defn config-handler  [{:keys [ctx]}]
  ;(response (:frontend-config ctx))
  {:status 200 :body (:frontend-config ctx)})