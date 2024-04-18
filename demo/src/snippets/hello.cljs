(ns snippets.hello)

(defn hello []
  [:p "hello, world!"])

(defn start [config]
  (println "starting snippets.hello service mode: " config)
  nil)