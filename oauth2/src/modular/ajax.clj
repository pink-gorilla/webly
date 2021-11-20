(ns modular.ajax
  (:require
   [taoensso.timbre :refer [info error]]
   [ajax.core :as ajax]))

(defn log-success [res]
  (info "ajax post success: " res))

(defn log-success [res]
  (error "ajax post error: " res))

; Authorization: "Basic " + base64encode(client_id + ":" + client_secret)

(defn auth-header [token]
  {"Authorization" (str "token " token)})

(defn POST
  [opts]
  (ajax/POST
    (merge {:method          :post
            :headers (auth-header)
            :format (ajax/json-request-format) ; {:keywords? true}
            :timeout         5000                     ;; optional see API docs
            :response-format (ajax/json-response-format {:keywords? true})
            :on-success     log-success
            :on-failure     log-error}
           opts)))



