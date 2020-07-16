(ns webly.resource-gorillaui-test
  (:require
   [clojure.string]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is testing]]))

(defn resource?  [name]
  (let [resource (io/resource name)]
    (if resource
      true
      false)))

; gorilla ui packages css/images (that cannot be used directly via shadow-cljs)
; in resources/public/npm-name/... 
(deftest gorilla-ui-resources []
  (is (= true (resource? "public/tailwindcss/dist/tailwind.css")))
  #_(is (= true (resource? "public/leaflet/dist/images/marker-icon.png"))))


