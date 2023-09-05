(ns frontend.helper
  (:require
   [taoensso.timbre :refer-macros [info infof error]]
   [clojure.string :as str]
   [cemerick.url :as url]))

(defn application-url []
  (url/url (-> js/window .-location .-href)))

(defn ws-origin [path app-url]
  (let [proto (if (= (:protocol app-url) "http") "ws" "wss")
        port-postfix  (let [port (:port app-url)]
                        (if (< 0 port)
                          (str ":" port)
                          ""))]
    (str proto ":" (:host app-url) port-postfix (str/replace (:path app-url) #"[^/]*$" path))))

(defn change-config [match]
  (str (second match) "config.edn"))

(defn config-path [path]
  (str/replace path
               #"(.*/)(.*)$"
               change-config))

(defn static-config-url []
  (let [{:keys [protocol port host path]} (application-url)
        port-postfix (if (< 0 port)
                       (str ":" port)
                       "")]
    (let [path-adjust (config-path path)]
      (str protocol "://" host port-postfix path-adjust))))

#_(defn sente-ws-url [db]
    (let [app-url (application-url)
          {:keys [protocol host port]} app-url
          proto (if (= protocol "http") "ws" "wss")
          path "/api/chsk"
          api (get-in db [:config :profile :server :api])
          port-api (get-in db [:config :web-server-api :port])
          port (if api port-api port)]
      (str protocol "://" host ":" port path)))
