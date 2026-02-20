(ns webly.dummy-test
  (:require
   [cljs.test :refer [deftest is testing run-tests]]))

(deftest add-test
  (testing "Basic addition"
    (is (= 4 (+ 2 2)))
    (is (= 0 (+ -1 1)))))
