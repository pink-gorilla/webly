(ns webly.config
  (:require
   [clojure.string] ; clj is empty otherwise
   #?(:cljs [webly.web.events-bidi]) ; side-effects
   ))

(def webly-config
  (atom {:title "webly"
         :timbre-loglevel :debug ; :info  ;  :trace :debug
         :prefix "/r/"
         :start  "demobad.core.init_BANG_ (); "
         :icon "/r/favicon.ico"}))


(defn res-href [href]
  (str (:prefix @webly-config) href))

(defn link-css [href]
  [:link {:rel "stylesheet"
          :href (res-href href)}])




