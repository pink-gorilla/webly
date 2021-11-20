(ns modular.webserver.handler.html
  (:require
   [ring.util.response :as response]))

(defn html-response [html]
  (response/content-type
   {:status 200
    :body html}
   "text/html"))

