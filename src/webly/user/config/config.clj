(ns webly.user.config.config
  (:require
   [clojure.java.io :as io]
   [cprop.core :refer [load-config]]
   [cprop.source :refer [from-env from-system-props from-resource from-file]]
   [ring.util.response :refer [response]]
   [webly.web.handler :refer [make-handler add-ring-handler]]
   [webly.web.middleware :refer [wrap-api-handler]]))

(def config-atom (atom {}))

(defn config-handler
  [_]
  (response @config-atom))

(add-ring-handler :webly/config (wrap-api-handler config-handler))

(defn from-file-exists [filename]
  (if (.exists (io/file filename))
    (from-file filename)
    {}))

(defn from-resource-exists [name]
  (let [r (io/resource name)]
    (if r
      (from-resource name)
      {})))

; https://github.com/tolitius/cprop
(defn- load-config-cprop []
  (load-config
   :resource "webly/config.edn" ; otherwise it would search for confg.edn 
   :merge
   [;(from-resource "webly/config.edn")
    (from-file-exists "config.edn")
    (from-file-exists "creds.edn")
    (from-resource-exists "creds.edn")
    ;(from-system-props)
    ;(from-env)
      ;(cli-args) 
    ]))


(defn load-config! []
  (let [config (load-config-cprop)]
    (println "config: " config)
    (reset! config-atom config)))


(comment

  (load-config)
  (load-config!)
  ;
  )

