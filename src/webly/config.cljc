(ns webly.config
  (:require
   [clojure.string] ; clj is empty otherwise
   #?(:cljs [pinkgorilla.ui.default-renderer]) ; side-effects gorilla-ui 
   #?(:cljs [webly.web.events-bidi]) ; side-effects
   ))

(def webly-config
  (atom {:title "webly"
         :timbre-loglevel :debug ; :info  ;  :trace :debug
         :start  "routing_example.core.init_BANG_ (); "
         :icon "/r/favicon.ico"}))




