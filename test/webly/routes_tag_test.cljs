(ns webly.routes-tag-test
  (:require
   [cljs.test :refer-macros [async deftest is testing]]
   [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :demo/main
        "x" :demo/x
        "z" (bidi/tag :demo/z :trott)}])

; tags can be used to pass non route-specific parameters to handler

(deftest route-tag-test []
  (is (= (bidi/match-route routes "/x") {:handler :demo/x}))
  (is (= (bidi/match-route routes "/z") {:handler :demo/z :tag :trott})))
