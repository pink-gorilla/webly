(ns modular.rest.lib.telegram
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [telegrambot-lib.core :as tbot]
   [modular.config :refer [get-in-config]]))

;; https://core.telegram.org/bots/api#getting-updates

;talk to: BotFather
;Bot usernames always end in 'bot' (e.g. @TriviaBot, @GitHub_bot).
; Use the /newbot command to create a new bot. The BotFather will ask you for a name and username, then generate an authentication token for your new bot.
; The name of your bot is displayed in contact details and elsewhere.
; The Username is a short name, to be used in mentions and t.me links. Usernames are 5-32 characters long and are case insensitive, but may only include Latin characters, numbers, and underscores. Your bot's username must end in 'bot', e.g. 'tetris_bot' or 'TetrisBot'.

; A command must always start with the '/' symbol and may not be longer than 32 characters. Commands can use latin letters, numbers and underscores. Here are a few examples:
; /get_messages_stats
; /set_timer 10min Alarm!
; /get_timezone London, UK

; a different api: https://github.com/Otann/morse

; ssl certs for morse:
; http://blog.hellonico.info/clojure/telegram_bot/

(defn telegram-bot-config []
  (let [my-token (get-in-config [:telegram/token])
        _ (info "telegram token: " my-token)
        mybot (tbot/create my-token)]
    mybot))
