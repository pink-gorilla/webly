(ns demo.handler.snippet
  (:require
   [clojure.pprint]
   [shadow.cljs.devtools.api :as shadow
    ;:refer [watch* worker-running?]
    ]))
(defn snippet-handler [req]
  (clojure.pprint/pprint req)
  (println "snippet: " (shadow/compile :webly {:verbose false}))
  {:status 200 :body "snippet"})