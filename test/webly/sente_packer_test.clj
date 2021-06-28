(ns webly.sente-packer-test
  (:require
   [taoensso.timbre :as log :refer [info infof]]
   [taoensso.sente.packers.transit :as sente-transit :refer [get-transit-packer]]
   [clojure.test :refer [deftest is testing]]
   [taoensso.sente.interfaces :as interfaces :refer (pack unpack)]
   [webly.web.encoding :as e]
   [webly.date :refer [now-local]]))

(println "encode: " e/encode)
(def p (get-transit-packer :json e/encode e/decode))

(def d (now-local))

(deftest sente-p []
  (is (= (pack p [:chsk/ws-ping "foo"]) "[\"~:chsk/ws-ping\",\"foo\"]"))
  (is (= (unpack p
                 (pack p  d)) d)))

;   (unpack tp (pack tp [:chsk/ws-ping "foo"]))
 ;   (enc/read-edn (enc/pr-edn ))))