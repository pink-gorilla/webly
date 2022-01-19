(ns rest.telegram
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [telegrambot-lib.core :as tbot]
   [modular.rest.lib.telegram :refer [telegram-bot-config]]))


(defn telegram []
  (let [mybot (telegram-bot-config)
        r1 (tbot/get-me mybot)
        _ (info "telegram result: " r1)
        ;r2 (tbot/set-webhook mybot "https://workflow.crbclean.com/api/telegram")
        r2 (tbot/set-webhook mybot "")
        _ (info "webhook result: " r2)
        r3 (tbot/get-updates mybot)
        _ (info "get-updates result: " r3)
        r4 (tbot/send-message mybot 1749435755 "hello, from crb!") ;  
        _ (info "send-message result: " r4)
        ]
  
))
