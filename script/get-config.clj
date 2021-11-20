

(require '[babashka.curl :as curl])
(require '[clojure.java.io :as io]) ;; optional
(require '[cheshire.core :as json]) ;; optional

(curl/get "https://localhost:8007/api/config")

; (curl/get "https://localhost:8000/api/config")

;;=> {:status 200, :body "200 OK", :headers { ... }}