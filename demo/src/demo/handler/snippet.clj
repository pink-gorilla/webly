(ns demo.handler.snippet
  (:require
   [clojure.pprint]
   [clojure.java.io :as io]
   [ring.util.response :as res]
   [ring.util.io :as ring-io]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]))
(defn snippet-handler [req]
  (clojure.pprint/pprint req)
  (println "snippet: " (shadow/compile :webly {:verbose false}))
  {:status 200 :body "snippet"})