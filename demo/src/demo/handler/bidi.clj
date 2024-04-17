(ns demo.handler.bidi
  (:require
   [ring.util.response :as res]
   [bidi.bidi :as bidi]))

(defn bidi-test-handler [req]
  (res/response {:bidi (bidi/tag :demo/z :trott)}))