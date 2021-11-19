(ns modular.oauth2.authorize.redirect
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [cemerick.url :as url]))

#_(defn message-event-handler
    [e]
    (info "message received: " e)
    (dispatch [:remote-oauth (.. e -data -code) (.. e -data -state)]))
;(js/window.addEventListener "message" message-event-handler)

; https://stackoverflow.com/questions/28230845/communication-between-tabs-or-windows
; https://developer.mozilla.org/en-US/docs/Web/API/Broadcast_Channel_API

(def bc (js/BroadcastChannel. "webly_oauth2_redirect_channel"))

(defn keywordize [my-map]
  (into {}
        (for [[k v] my-map]
          [(keyword k) v])))

(defn url-data [wurl]
  (let [url (url/url wurl)]
    {:anchor (keywordize (url/query->map (:anchor url)))
     :query (keywordize (:query url))}))

(defn register-callback [cb]
  (set! (.. bc -onmessage)
        (fn [ev]
          (let [json (. ev -data)
                data (js->clj (.parse js/JSON json))
                wurl (get data "url")
                provider (get data "provider")
                cbdata (merge  (url-data wurl) {:provider (keyword provider)})]
            (info "oauth chan rcvd: json: " json  "data: " data)
            (info "oauth cb data: " cbdata)
            (cb cbdata)))))

;; to be used from redirect window.
;; this is not used in production, as it would require the full bundle to be
;; loaded in the redirect window.
;; we load a javascript file, that does the same thing.

(defn window-url []
  (.. js/window -location -href))

(defn ^:export sendcallback [provider]
  (let [wurl (window-url)
        str (.stringify js/JSON #js {:provider provider :url wurl})]
    (println "sending message: " str)
    (.postMessage bc str)))



