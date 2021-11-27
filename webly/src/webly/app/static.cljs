(ns webly.app.static
  (:require
   [taoensso.timbre :refer-macros [info warn]]
   [clojure.string :as str]
   [cemerick.url :as url]
   [frontend.helper :refer [application-url]]
   [webly.build.prefs :refer [pref]]))

(defn static?
  (atom false))

(defn change-config [match]
  (str (second match) ""))

(defn entry-path []
  (let [{:keys [protocol port host path]} (application-url)]
   ;(str/replace path #"(.*/)(.*)$" change-config)
    path))

(defn make-static-adjustment []
  (info "making static adjustments..")
  (info "static entry path is: " (entry-path))
  (info "static prefs: " (prefs))
  (reset! static? true))
