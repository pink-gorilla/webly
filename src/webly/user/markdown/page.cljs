(ns webly.user.markdown.page
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [webly.web.handler :refer [reagent-page]]
   [webly.util :refer [lazy-component show-lazy]]))

; https://github.com/thheller/code-splitting-clojurescript/blob/master/src/main/demo/app.cljs

(def markdown-explorer-react (lazy-component webly.user.markdown.views/markdown-explorer-react))

#_(defn markdown-page [{:keys [route-params query-params handler] :as args}]
    (debug "md page args: " args)
    (let [{:keys [file]} route-params]
      [markdown-explorer file]))

(defmethod reagent-page :ui/markdown [{:keys [route-params query-params handler] :as args}]
  (info "rendering md page with args: " args)
  (let [{:keys [file]} route-params]
    ;[markdown-file file]
    [show-lazy markdown-explorer-react {:file file}]))



;[show-lazy
;    [markdown-page args]]) ; @query-params

