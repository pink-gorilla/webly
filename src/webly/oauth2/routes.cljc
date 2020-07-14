(ns webly.oauth2.routes
  (:require
   [clojure.string] ; no empty require for cljs
   ))

(def routes-oauth2
  {;"test"           :webly/oauth2
   "github/auth"     :webly/oauth2
   "github/callback" :webly/oauth2
   ;"github/landing"  :webly/oauth2
   })

