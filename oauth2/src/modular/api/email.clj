(ns modular.api.email
  (:require
   [postal.core :as postal]
   [modular.config :refer [get-in-config]]))

; https://github.com/drewr/postal

(defn send-email [msg]
  (let [smtp-config (get-in-config [:smtp :config])
        from (get-in-config [:smtp :from])
        msg (assoc msg :from from)]
    (postal/send-message smtp-config msg)))
