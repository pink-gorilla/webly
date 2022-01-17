(ns rest.telegram
 (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [telegrambot-lib.core :as tbot]
   [modular.config :refer [get-in-config]]
))


; a different api: https://github.com/Otann/morse

; ssl certs for morse:
; http://blog.hellonico.info/clojure/telegram_bot/


(defn telegram []
  (let [my-token (get-in-config [:telegram/token])
  _ (info "telegram token: " my-token)
        mybot (tbot/create my-token)
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
