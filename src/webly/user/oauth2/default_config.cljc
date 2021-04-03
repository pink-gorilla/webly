(ns webly.user.oauth2.default-config
  (:require
   [webly.user.oauth2.routes] ; side-effects
   #?(:clj  [webly.user.oauth2.handler]) ; side-effects
   #?(:cljs [webly.user.oauth2.view]) ; side effects
   ))