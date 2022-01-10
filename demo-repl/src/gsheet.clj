(ns gsheet
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.request :refer [get-endpoint get-request get-request-xero 
                                   post-request put-request]]))



(defn modify-cells []
 (let [sheet-id "1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8"]
  (put-request
   :google
   (str "https://sheets.googleapis.com/v4/spreadsheets/"
        sheet-id
        "/values/Sheet1!A1:D5?valueInputOption=USER_ENTERED")
    {:range "Sheet1!A1:D5"
     :majorDimension "ROWS"
     :values [["Item" "Cost" "Stocked" "Ship Date"]
              ["Wheel" "$20.50" "4" "3/1/2016"]
              ["Door" "$15" "2" "3/15/2016"]
              ["Engine" "$100" "1" "3/20/2016"]
              ["Totals" "=SUM(B2:B4)" "=SUM(C2:C4)" "=MAX(D2:D4)"]]}
    )))


;(defn post-cells []
; (post-request 
;     :google
;     "https://sheets.googleapis.com/v4/spreadsheets/1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8:batchUpdate"
;      {:requests [{:range "A1:B2" 
;                   :values [["Test" "5"]
;                            ["bodrum" "4"]]}]}
;   ))

