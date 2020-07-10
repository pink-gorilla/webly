(ns pinkgorilla.explorer.bidi
  (:require
   [taoensso.timbre :refer-macros [debug info error]]
   [re-frame.core :refer [dispatch]]
   [cemerick.url :as url]
   [pinkgorilla.storage.protocols :refer [gorilla-path]]))

(defn subs2 [s start]
  (.substring s start (count s)))

(defn goto-notebook! [storage]
  (let [query-params (gorilla-path storage)
        query-params (url/query->map (subs2 query-params 1))
        _ (info "goto-notebook query params: " query-params)]
    (dispatch [:bidi/goto :ui/notebook query-params])))




