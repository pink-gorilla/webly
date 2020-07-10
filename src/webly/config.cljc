(ns webly.config
  (:require
   [clojure.string] ; clj is empty otherwise
   #?(:cljs [pinkgorilla.ui.default-renderer]) ; side-effects gorilla-ui 
   #?(:cljs [webly.web.events-bidi]) ; side-effects
   #?(:cljs [webly.user.auth.view]) ; side effects
   ))


(def webly-config
  (atom {:title "webly"}))


; ROUTES:
; route definitions can be composed with bidi. 
; Therefore it does make sense that default route config is
; exported here. 

(def explorer-routes-ui
  {"explorer"     :ui/explorer
   "notebook"     :ui/notebook})

(def explorer-routes-api
  {"/api/" {"explorer"  {:get  :api/explorer}
            "notebook"  {:get  :api/notebook-load
                         :post :api/notebook-save}}})



