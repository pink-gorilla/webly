(ns webly.routes-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [webly.web.resources]
   [demo.routes :refer [demo-routes-backend]]))

(def routes demo-routes-backend)

(deftest handler->path []
  (is (= (bidi/path-for routes :demo/main) "/"))
  (is (= (bidi/path-for routes :demo/help) "/help"))
  (is (= (bidi/path-for routes :api/test) "/api/test"))
  (is (= (bidi/path-for routes :api/time) "/api/time")))

(defn GET [url]
  (bidi/match-route routes url :request-method :get))

(defn POST [url]
  (bidi/match-route routes url :request-method :post))

(deftest path->handler []
  (is (= (:handler (GET "/")) :demo/main))
  (is (= (:handler (GET "/help")) :demo/help))
  (is (= (:handler (GET "/api/time")) :api/time))
  (is (= (:handler (POST "/api/test")) :api/test)))

(deftest resource-routes []
  (is (not (= (:handler (GET "/r/favicon.ico")) nil)))
  (is (= (GET "/r/not-found.jpg") nil)))



