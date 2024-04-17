(ns webly.routes-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [ring.mock.request :refer [request] :rename {request mock-request}]
   [bidi.bidi :as bidi]
   [webly.spa.handler.handler]
   [webly.routes :refer [routes-api routes-app]]
   [webly.spa.handler.routes :refer [make-routes-backend make-routes-frontend]]))

(def routes-backend (make-routes-backend routes-api {} {}))
(def routes-frontend (make-routes-frontend routes-app))

(deftest handler->path []
  (is (= (bidi/path-for routes-frontend :demo/main) "/"))
  (is (= (bidi/path-for routes-frontend :demo/help) "/help"))
  (is (= (bidi/path-for routes-frontend :demo/party :location "Vienna") "/party/Vienna"))
  ;(is (= (bidi/path-for routes-backend 'demo.handler.test/test-handler) "/api/test"))
  ;(is (= (bidi/path-for routes-backend 'demo.handler/time-handler) "/api/time"))
  )

(defn GET [url]
  (bidi/match-route routes-backend url :request-method :get))

(defn POST [url]
  (bidi/match-route routes-backend url :request-method :post))

#_(deftest path->handler []
  ;(is (= (:handler (GET "/")) :demo/main))
  ;(is (= (:handler (GET "/help")) :demo/help))
    (is (= (:handler (GET "/api/time")) 'demo.handler/time-handler))
    (is (= (:handler (POST "/api/test")) 'demo.handler.test/test-handler)))

(deftest resource-routes []
  (is (not (= (:handler (GET "/r/webly/icon/silver.ico")) nil)))
  (is (= (:handler (GET "/r/not-found.jpg")) nil)))



