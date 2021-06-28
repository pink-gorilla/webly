(ns repl1
  (:require
   [bidi.bidi :as bidi]))

(def routes
  ["/" {"" :demo/main
        "x" :demo/bongo
        "y" {:matched :demo/y :tag :uu}
        "z" (bidi/tag :demo/z :bongo)}])

(meta  (bidi/tag :demo/z :bongo))

(pr-str routes)


(bidi/match-route routes "/x")
(bidi/match-route routes "/y")
(bidi/match-route routes "/z")

(bidi/match-route routes "/x" :request-method :get)

(bidi/path-for routes :demo/main)