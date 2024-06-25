(ns webly.encoding-sente-packer-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [taoensso.sente.packers.transit :as sente-transit :refer [get-transit-packer]]
   [taoensso.sente.interfaces :as interfaces :refer (pack unpack)]
   [modular.encoding.transit :as e]
   [modular.date :refer [now-date]]))

(def p (get-transit-packer :json e/encode e/decode))

(def d (now-date))

(deftest sente-p []
  (is (= (pack p [:chsk/ws-ping "foo"]) "[\"~:chsk/ws-ping\",\"foo\"]"))
  (is (= (unpack p
                 (pack p  d)) d)))

;   (unpack tp (pack tp [:chsk/ws-ping "foo"]))
 ;   (enc/read-edn (enc/pr-edn ))))