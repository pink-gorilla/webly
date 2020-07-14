(ns webly.oauth2.default-config
  (:require
   [webly.oauth2.routes] ; side-effects
   #?(:clj  [webly.oauth2.handler]) ; side-effects
   #?(:cljs [webly.oauth2.view]) ; side effects
   ))