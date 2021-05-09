(ns webly.config-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [webly.config :refer [load-config! config-atom]]))

(defn c [c]
  (reset! config-atom {})
  (load-config! c)
  (-> @config-atom
      (dissoc :jetty-ws
              :settings
              :web-server-api
              :google-analytics
              :webly
              :prefix
              :oauth2
              :web-server
              :shadow
              :keybindings
              :timbre-loglevel)))

(deftest config []
  (is (= {}  (c nil))) ; nil works
  (is (= {:a 1}  (c {:a 1}))) ; map is taken as-is
  (is (= {:a 1 :b 2}  (c [{:a 1 :b 1} {:a 1 :b 2}]))) ; vec -> later config overrides
  (is (= {:c 3}  (c "bongo.edn"))) ; strings as resources
  (is (= {:a 7 :c 3} (c [{:a 7} "bongo.edn"]))) ; strings as resources
  )