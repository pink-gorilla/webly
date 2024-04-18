(ns snippets.hello)

(defn hello []
  [:p "hello, world!"])

(defn start [mode]
  (println "starting snippets.hello service mode: " mode)
  nil)