(ns unused.css-via-get)

(ns pinkgorilla.notebook-ui.events.events-theme
  (:require
   [taoensso.timbre :as timbre :refer [debug warn error] :refer-macros [info errorf]]
   [cljs.reader]
   [cljs.tools.reader]
   [re-frame.core :refer [reg-event-db dispatch dispatch-sync]]
   [ajax.core :refer [GET]]))

(defn add-style [css-text]
  (let [new-style (js/document.createElement "style")]
    ;(set! (.-id new-style) "mystyle")
    (.appendChild new-style (js/document.createTextNode css-text))
    (.appendChild (.-head js/document) new-style)))

(def base "http://localhost:8000/r/")

(defn handler [response]
  (warn "css loaded: " (count response))
  (add-style response))

(defn error-handler [{:keys [status status-text]}]
  (error (str "something bad happened: " status " " status-text)))

(reg-event-db
 :browser/load-css
 (fn [db [link]]
   (warn "loading css: " link)
   (GET (str base link-codemirror-css) {:handler handler
                                        :error-handler error-handler})
   db))


; (dispatch [:browser/load-css link-codemirror-css])

