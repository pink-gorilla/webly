(ns webly.oauth2.routes
  (:require
   [clojure.string] ; no empty require for cljs
   #?(:clj [webly.oauth2.handler]) ; side-effects
   #?(:cljs [webly.oauth2.view]) ; side effects
   ))

; TODO: oauth callbacks
;   {:route/url  "/foursquare-hello"
;    :route/page foursquare/hello-page}


(def routes-oauth2
  {"test"                   :webly/oauth2
   "oauth2/github"          :webly/oauth2
   "oauth2/github/callback" :webly/oauth2
   "my"                     :webly/oauth2})