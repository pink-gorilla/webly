(ns webly.app.mode.url
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string :refer [last-index-of]]
   [cemerick.url :as curl]))

(defn application-url []
  (-> js/window .-location .-href))

(defn app-load-path []
  (let [url (application-url)
        url-base (subs url 0 (last-index-of url "/"))]
    (info "app-load-path: " url-base)
    url-base))

(def dynamic-base app-load-path)

(defn entry-path []
  (let [{:keys [protocol port host path]} (application-url)]
   ;(str/replace path #"(.*/)(.*)$" change-config)
    path))