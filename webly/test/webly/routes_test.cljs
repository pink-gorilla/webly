(ns webly.routes-test
  (:require
   [cljs.test :refer-macros [async deftest is testing]]
   [bidi.bidi :as bidi]))

(def routes-frontend
  ["/" {"" :demo/main
        "bongo" :demo/bongo}])

(deftest handler->path []
  (is (= (bidi/path-for routes-frontend :demo/main) "/"))
  (is (= (bidi/path-for routes-frontend :demo/bongo) "/bongo")))
