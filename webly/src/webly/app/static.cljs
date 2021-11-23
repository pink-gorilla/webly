(ns webly.app.static
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [clojure.string :as str]
   [cemerick.url :as url]
   [frontend.config.core :refer [webly-mode-atom entry-path-atom]]
   [frontend.helper :refer [application-url]]))

(defn change-config [match]
  (str (second match) ""))

(defn entry-path []
  (let [{:keys [protocol port host path]} (application-url)]
   ;(str/replace path #"(.*/)(.*)$" change-config)
    path))

(defn entry-path-adjust [path]
  (if (str/blank? @entry-path-atom)
    path
    (if (str/ends-with? @entry-path-atom "/")
      (str/replace path @entry-path-atom "/")
      (str/replace path @entry-path-atom ""))

    ;"/"
    ))

(defn make-static-adjustment []
  (info "making static adjustments..")
  (info "static entry path is: " (entry-path))
  (reset! entry-path-atom (entry-path))
  (reset! webly-mode-atom :static))
