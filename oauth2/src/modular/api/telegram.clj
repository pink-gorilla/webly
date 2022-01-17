(ns modular.api.telegram
  (:require
   [clj-http.client :as client]
   [clojure.string :refer [escape join]]))

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

(def ^:private api-url "https://api.telegram.org/bot%s/%s")

(defn html-parse-mode
  "Formats html message."
  [e]
  (str
   "<strong>Host:</strong> " (or (:host e) "-") "\n"
   "<strong>Service:</strong> " (or (:service e) "-") "\n"
   "<strong>State:</strong> " (or (:state e) "-") "\n"
   "<strong>Metric:</strong> " (or (:metric e) "-") "\n"
   "<strong>Description:</strong> " (or (:description e) "-")))

(defn markdown-parse-mode
  "Formats markdown message."
  [e]
  (str
   "*Host:* " (or (:host e) "-") "\n"
   "*Service:* " (or (:service e) "-") "\n"
   "*State:* " (or (:state e) "-") "\n"
   "*Metric:* " (or (:metric e) "-") "\n"
   "*Description:* " (or (:description e) "-")))

(defn- format-message
  "Formats a message."
  [parse-mode message-formatter event]
  (cond
    message-formatter (message-formatter event)
    (= "HTML" parse-mode) (html-parse-mode event)
    :default (markdown-parse-mode event)))

(defn- post
  "POST to the Telegram API."
  [event {:keys [token http-options telegram-options message-formatter]
          :or {http-options {}}}]
  (let [parse-mode (:parse_mode telegram-options "Markdown")
        text (format-message parse-mode message-formatter event)
        form-params (assoc telegram-options
                           :parse_mode parse-mode
                           :text text)]
    (client/post (format api-url token "sendMessage")
                 (merge {:form-params form-params
                         :throw-entire-message? true}
                        http-options))))

(defn telegram
  "Send events to Telegram chat. Uses your bot token and returns a function,
  which send message through API to specified chat.
  Format event (or events) to string with markdown syntax by default.
  Telegram bots API documentation: https://core.telegram.org/bots/api
  Options:
  - `:token`              The telegram token
  - `:http-options`       clj-http extra options (optional)
  - `:telegram-options`   These options are merged with the `:form-params` key of
  the request. The `:chat_id` key is mandatory.
  By default, the `:parse_mode` key is \"Markdown\".
  - `:message-formatter`  A function accepting an event and returning a string. (optional).
  If not specified, `html-parse-mode` or `markdown-parse-mode` will be used,
  depending on the `:parse_mode` value.
  Usage:
  ```clojure
  (def token \"define_your_token\")
  (def chat-id \"0123456\")
  (streams
    (telegram {:token token
               :telegram-options {:chat_id chat-id
                                  :parse_mode \"HTML\"}}))
  ```"
  [opts]
  (fn [event]
    (let [events (if (sequential? event) event [event])]
      (doseq [event events]
        (post event opts)))))

; https://api.telegram.org/bot123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11/getMe