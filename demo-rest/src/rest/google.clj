(ns rest.google
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]

   [modular.oauth2.token.store :refer [load-token]]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]
   [modular.oauth2.request :refer [get-request post-request put-request]]))



(defn gsheet-modify-cells []
 (let [sheet-id "1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8"]
  (put-request
   :google/sheets
    {:range "Sheet1!A1:D5"
     :majorDimension "ROWS"
     :values [["Item" "Cost" "Stocked" "Ship Date"]
              ["Wheel" "$320.50" "4" "3/1/2016"]
              ["Door" "$15" "2" "3/15/2016"]
              ["Engine" "$100" "1" "3/20/2016"]
              ["Totals" "=SUM(B2:B4)" "=SUM(C2:C4)" "=MAX(D2:D4)"]]}
    {}
    (str sheet-id "/values/Sheet1!A1:D5?valueInputOption=USER_ENTERED")

    )))


;(defn post-cells []
; (post-request 
;     :google
;     "https://sheets.googleapis.com/v4/spreadsheets/1oO-UlrkEwaED_fKcNm27lIN9EdcbwF8iCS5UiYFOnk8:batchUpdate"
;      {:requests [{:range "A1:B2" 
;                   :values [["Test" "5"]
;                            ["bodrum" "4"]]}]}
;   ))

(defn google []
  (refresh-auth-token :google)
  (info "google/userinfo: " (get-request :google/userinfo))
  #_(->> (get-request :google/drive-files-list)
       :files
       (map #(dissoc % :kind :id :mimeType))
       (print-table))

 ; (get-request :google/search {:q "clojure" :num 10})
  (gsheet-modify-cells)
  

  ;
  )
