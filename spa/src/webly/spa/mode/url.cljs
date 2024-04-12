(ns webly.spa.mode.url
  (:require
   [taoensso.timbre :as timbre :refer-macros [debug debugf info warn error]]
   [clojure.string :refer [last-index-of ends-with?]]
   [cemerick.url :refer [url]]))

(defn current-url []
  (-> js/window .-location .-href))

(defn app-load-path []
  (let [url (current-url)
        url-base (subs url 0 (last-index-of url "/"))]
    (info "app-load-path: " url-base)
    url-base))

;(def dynamic-base app-load-path)

(defn current-path []
  (let [{:keys [protocol port host path]} (-> (current-url) (url))]
    path))

(defn entry-path []
  (let [index-html "index.html"
        path (current-path)]
   ;(str/replace path #"(.*/)(.*)$" change-config)
    (if (ends-with? path index-html)
      (subs path 0 (- (count path) (count index-html)))
      path)))

(defn entry-path-full []
  (let [path (entry-path )
        full (-> (current-url) (url))
        full-path (assoc full :path path)
        url (.toString full-path)
        ]
    (if (ends-with? url "/")
      (subs url 0 (dec (count url)))
      url)))