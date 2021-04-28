(ns webly.date-test
  (:require
     #?(:clj [clojure.test :refer [deftest is are testing]]
        :cljs [cljs.test :refer-macros [async deftest is testing]])
   [webly.date :refer [date-time tostring]]))



(def dt  
  (date-time 2010 10 3 11 22 33))

(def dts "2010-10-03 11:22:33")

(deftest dt-format []
  (is (= (tostring dt) dts))
 )
